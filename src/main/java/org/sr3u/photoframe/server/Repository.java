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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.misc.util.DateUtil;
import org.sr3u.photoframe.misc.util.ImageUtil;
import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.data.Item;
import org.sr3u.photoframe.server.data.MediaType;
import org.sr3u.photoframe.server.events.*;
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

    private static final Logger log = LogManager.getLogger(Repository.class);

    private static final int MAX_ATTEMPTS = 16;
    private final PhotosLibraryClient gClient;
    private final Image defaultImage;
    private final Dao<Item, String> dao;
    private Dimension defaultSize = new Dimension(1024, 1024);
    private EventSystem eventSystem;

    @SneakyThrows
    public Repository(PhotosLibraryClient gClient, EventSystem eventSystem) {
        this.gClient = gClient;
        defaultImage = ImageUtil.scaledImage(ImageIO.read(Repository.class.getResource("picture.png")), defaultSize);
        Class.forName("org.sqlite.JDBC");
        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + Main.settings.getMedia().getDatabasePath());
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
                    random = dao.queryBuilder().orderByRaw("RANDOM()")
                            .where()
                            .eq("mediaType", MediaType.IMAGE)
                            .queryForFirst();
                }
                MediaItem mediaItem = gClient.getMediaItem(random.getGoogleID());
                BufferedImage image = ImageIO.read(new URL(mediaItem.getBaseUrl() + ImageUtil.googlePhotoSize(size)));
                MediaMetadata metadata = mediaItem.getMediaMetadata();
                return new ImageWithMetadata(image, ImageWithMetadata.convert(metadata));
            } catch (Exception e) {
                log.error(e);
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
            log.info("Refresh: STARTED");
            String albumName = Main.settings.getMedia().getAlbumName();
            refresh(albumName, true);
            if (isNotEmpty(albumName) && Main.settings.getMedia().isBackup()) {
                log.info("Refresh: Getting all media, for backup purposes");
                refresh(null, false);
            }
            log.info("Refresh: CLEANUP");
            cleanup();
        } catch (Throwable e) {
            log.error(e);
            e.printStackTrace();
        }
        log.info("Refresh: DONE");
    }

    private void refresh(String albumName, boolean display) {
        Date refreshStartDate = new Date();
        while (refreshStartDate != null) {
            refreshStartDate = refresh(refreshStartDate, albumName, display);
        }
    }

    private void cleanup() {
        long cleanupTimestamp = DateUtil.timestamp(new Date());
        try {
            Streamex.ofCollection(dao.queryBuilder().where().lt("validUntil", cleanupTimestamp).query())
                    .forEach(item -> eventSystem.fireEvent(new DeletedItemEvent(item, gClient, dao)));
            DeleteBuilder<Item, String> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().lt("validUntil", cleanupTimestamp);
            synchronized (this) {
                deleteBuilder.delete();
            }
        } catch (SQLException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private Date refresh(Date refreshStartDate, String albumName, boolean display) {
        Iterator<MediaItem> iterator = getMediaIterator(refreshStartDate, albumName);
        Date refreshStarted = new Date();
        long validUntil = Item.defaultCleanupTimestamp();
        while (iterator.hasNext()) {
            MediaItem next = iterator.next();
            if (DateUtil.isDateExpiredForMediaItem(refreshStarted)) {
                Date refreshDate = DateUtil.addDays(DateUtil.getCreationDate(next.getMediaMetadata().getCreationTime()), 1);
                log.info("Refresh restarted from date " + refreshDate);
                return refreshDate;
            }
            try {
                log.info("processing item " + next.getId());
                synchronized (this) {
                    List<Item> item = dao.queryForEq("googleID", next.getId());
                    if (item.isEmpty()) {
                        Item newItem = new Item(next, display);
                        if (display) {
                            dao.create(newItem);
                            eventSystem.fireEvent(new NewItemEvent(refreshStarted, newItem, next, gClient, dao));
                        } else {
                            eventSystem.fireEvent(new RetrievedItemEvent(refreshStarted, newItem, next, gClient, dao));
                        }
                    } else {
                        Item item1 = item.get(0);
                        if (display) {
                            item1.setValidUntil(validUntil);
                            dao.update(item1);
                            eventSystem.fireEvent(new UpdatedItemEvent(refreshStarted, item1, next, gClient, dao));
                        } else {
                            eventSystem.fireEvent(new RetrievedItemEvent(refreshStarted, item1, next, gClient, dao));
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(e);
                e.printStackTrace();
            }
        }
        return null;
    }

    private Iterator<MediaItem> getMediaIterator(Date refreshStartDate, String albumName) {
        Iterator<MediaItem> iterator;
        if (isNotEmpty(albumName)) {
            Iterable<MediaItem> albumMedia = getAlbumMedia(refreshStartDate, albumName);
            if (albumMedia == null) {
                iterator = getAllMedia(refreshStartDate).iterator();
            } else {
                iterator = albumMedia.iterator();
            }
        } else {
            iterator = getAllMedia(refreshStartDate).iterator();
        }
        return iterator;
    }

    private boolean isNotEmpty(String albumName) {
        return albumName != null && !albumName.trim().isEmpty();
    }

    private Iterable<MediaItem> getAllMedia(Date refreshStartDate) {
        Filters filters = getFilters(refreshStartDate);
        InternalPhotosLibraryClient.SearchMediaItemsPagedResponse listMediaItems = gClient.searchMediaItems(filters);
        return listMediaItems.iterateAll();
    }

    private Iterable<MediaItem> getAlbumMedia(Date refreshStartDate, String albumName) {
        Iterable<Album> albums = gClient.listAlbums().iterateAll();
        for (Album album : albums) {
            if (albumName.equals(album.getTitle())) {
                return gClient.searchMediaItems(album.getId()).iterateAll();
            }
        }
        Iterable<Album> sharedAlbums = gClient.listSharedAlbums().iterateAll();
        for (Album album : sharedAlbums) {
            if (albumName.equals(album.getTitle())) {
                return gClient.searchMediaItems(album.getId()).iterateAll();
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
