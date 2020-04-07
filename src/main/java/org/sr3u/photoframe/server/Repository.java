package org.sr3u.photoframe.server;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;
import lombok.SneakyThrows;
import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.data.Item;
import org.sr3u.photoframe.server.data.MediaType;
import org.sr3u.photoframe.server.events.DeletedItemEvent;
import org.sr3u.photoframe.server.events.EventSystem;
import org.sr3u.photoframe.server.events.NewItemEvent;
import org.sr3u.photoframe.server.events.UpdatedItemEvent;
import org.sr3u.photoframe.server.util.DateUtil;
import org.sr3u.photoframe.server.util.ImageUtil;
import sr3u.streamz.streams.Streamex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static com.google.photos.library.v1.proto.ContentCategory.*;

public class Repository {
    private static final int MAX_ATTEMPTS = 16;
    private final PhotosLibraryClient gClient;
    private final Image defaultImage;
    private final Dao<Item, String> dao;
    private Dimension defaultSize = new Dimension(1024, 1024);
    private EventSystem eventSystem;

    @SneakyThrows
    public Repository(PhotosLibraryClient gClient, EventSystem eventSystem) {
        this.gClient = gClient;
        defaultImage = ImageUtil.scaledImage(ImageIO.read(Repository.class.getResource("picture.svg")), defaultSize);
        Class.forName("org.sqlite.JDBC");
        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + Server.settings.getMedia().getDatabasePath());
        TableUtils.createTableIfNotExists(connectionSource, Item.class);
        dao = DaoManager.createDao(connectionSource, Item.class);
        this.eventSystem = eventSystem;
    }

    public ImageWithMetadata getRandom() {
        return getRandom(defaultSize);
    }

    public ImageWithMetadata getRandom(Dimension size) {
        if (gClient == null) {
            return new ImageWithMetadata(defaultImage, Collections.emptyMap());
        }
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {
                Item random;
                synchronized (this) {
                    random = dao.queryBuilder().orderByRaw("RANDOM()").where().eq("mediaType", MediaType.IMAGE).queryForFirst();
                }
                MediaItem mediaItem = gClient.getMediaItem(random.getGoogleID());
                BufferedImage image = ImageIO.read(new URL(mediaItem.getBaseUrl() + ImageUtil.googlePhotoSize(size)));
                MediaMetadata metadata = mediaItem.getMediaMetadata();
                return new ImageWithMetadata(image, ImageWithMetadata.convert(metadata));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ImageWithMetadata(defaultImage, Collections.emptyMap());
    }

    public void refresh() {
        try {
            if (gClient == null) {
                return;
            }
            System.out.println(getClass().getName() + " Refresh: STARTED");
            Date refreshStartDate = new Date();
            while (refreshStartDate != null) {
                refreshStartDate = refresh(refreshStartDate);
            }
            cleanup();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName() + " Refresh: DONE");
    }

    private void cleanup() {
        long cleanupTimestamp = DateUtil.timestamp(new Date());
        try {
            Streamex.ofStream(dao.queryBuilder().where().lt("validUntil", cleanupTimestamp).query().stream())
                    .forEach(item -> eventSystem.fireEvent(new DeletedItemEvent(item, gClient, dao)));
            DeleteBuilder<Item, String> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().lt("validUntil", cleanupTimestamp);
            synchronized (this) {
                deleteBuilder.delete();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Date refresh(Date refreshStartDate) {
        Filters filters = getFilters(refreshStartDate);
        Date refreshStarted = new Date();
        InternalPhotosLibraryClient.SearchMediaItemsPagedResponse listMediaItems = gClient.searchMediaItems(filters);
        Iterator<MediaItem> iterator = listMediaItems.iterateAll().iterator();
        long validUntil = Item.defaultCleanupTimestamp();
        while (iterator.hasNext()) {
            MediaItem next = iterator.next();
            if (DateUtil.isDateExpiredForMediaItem(refreshStarted)) {
                Date refreshDate = DateUtil.addDays(DateUtil.getCreationDate(next.getMediaMetadata().getCreationTime()), 1);
                System.out.println("Refresh restarted from date " + refreshDate);
                return refreshDate;
            }
            try {
                System.out.println("processing item " + next.getId());
                synchronized (this) {
                    List<Item> item = dao.queryForEq("googleID", next.getId());
                    if (item.isEmpty()) {
                        Item newItem = new Item(next);
                        dao.create(newItem);
                        eventSystem.fireEvent(new NewItemEvent(refreshStarted, newItem, next, gClient, dao));
                    } else {
                        Item item1 = item.get(0);
                        item1.setValidUntil(validUntil);
                        eventSystem.fireEvent(new UpdatedItemEvent(refreshStarted, item1, next, gClient, dao));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Filters getFilters(Date endDate) {
        MediaTypeFilter mediaTypeFilter = MediaTypeFilter.newBuilder()
                .addMediaTypes(MediaTypeFilter.MediaType.ALL_MEDIA)
                .build();
        ContentFilter contentFilter = ContentFilter.newBuilder()
                .addAllExcludedContentCategories(Arrays.asList(RECEIPTS, DOCUMENTS, SCREENSHOTS))
                .build();
        com.google.type.Date earliest = com.google.type.Date.newBuilder()
                .setDay(1)
                .setMonth(1)
                .setYear(1)
                .build();
        com.google.type.Date endDateP = DateUtil.toProtobuf(endDate);
        DateFilter dateFilter = DateFilter.newBuilder()
                .addRanges(DateRange.newBuilder()
                        .setStartDate(earliest)
                        .setEndDate(endDateP)
                        .build())
                .build();
        return Filters.newBuilder()
                .setMediaTypeFilter(mediaTypeFilter)
                .setContentFilter(contentFilter)
                .setIncludeArchivedMedia(false)
                .setDateFilter(dateFilter)
                .build();
    }

}
