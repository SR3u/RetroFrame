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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static com.google.photos.library.v1.proto.ContentCategory.*;

public class Repository {
    private static final int MAX_ATTEMPTS = 16;
    private final PhotosLibraryClient gClient;
    private final BufferedImage defaultImage;
    private final Dao<Item, String> dao;
    private Dimension defaultSize = new Dimension(1024, 1024);

    @SneakyThrows
    public Repository(PhotosLibraryClient gClient) {
        this.gClient = gClient;
        defaultImage = ImageUtil.scaledImage(ImageIO.read(Repository.class.getResource("picture.svg")), defaultSize);
        Class.forName("org.sqlite.JDBC");
        JdbcConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:mediaItems.db");
        TableUtils.createTableIfNotExists(connectionSource, Item.class);
        dao = DaoManager.createDao(connectionSource, Item.class);
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
                Item random = dao.queryBuilder().orderByRaw("RANDOM()").queryForFirst();
                MediaItem mediaItem = gClient.getMediaItem(random.getGoogleID());
                BufferedImage image = ImageIO.read(new URL(mediaItem.getBaseUrl() + googlePhotoSize(size)));
                MediaMetadata metadata = mediaItem.getMediaMetadata();
                return new ImageWithMetadata(image, ImageWithMetadata.convert(metadata));
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return new ImageWithMetadata(defaultImage, Collections.emptyMap());
    }

    private String googlePhotoSize(Dimension size) {
        return "=w" + size.width + "-h" + size.height;
    }

    public void refresh() {
        try {
            if (gClient == null) {
                return;
            }
            System.out.println(getClass().getName() + " Refresh: STARTED");
            long cleanupTimestamp = DateUtil.timestamp(new Date());
            try {
                DeleteBuilder<Item, String> deleteBuilder = dao.deleteBuilder();
                deleteBuilder.where().lt("validUntil", cleanupTimestamp);
                deleteBuilder.delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MediaTypeFilter mediaTypeFilter = MediaTypeFilter.newBuilder()
                    .addMediaTypes(MediaTypeFilter.MediaType.PHOTO)
                    .build();
            ContentFilter contentFilter = ContentFilter.newBuilder()
                    .addAllExcludedContentCategories(Arrays.asList(RECEIPTS, DOCUMENTS, SCREENSHOTS))
                    .build();
            Filters filters = Filters.newBuilder()
                    .setMediaTypeFilter(mediaTypeFilter)
                    .setContentFilter(contentFilter)
                    .setIncludeArchivedMedia(false)
                    .build();
            InternalPhotosLibraryClient.SearchMediaItemsPagedResponse listMediaItems = gClient.searchMediaItems(filters);
            Iterator<MediaItem> iterator = listMediaItems.iterateAll().iterator();
            long validUntil = Item.defaultCleanupTimestamp();
            while (iterator.hasNext()) {
                MediaItem next = iterator.next();
                try {
                    System.out.println("processing item " + next.getId());
                    List<Item> item = dao.queryForEq("googleID", next.getId());
                    if (item.isEmpty()) {
                        dao.create(new Item(next));
                    } else {
                        item.get(0).setValidUntil(validUntil);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println(getClass().getName() + " Refresh: DONE");
    }

}
