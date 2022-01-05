package org.sr3u.photoframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.MediaItem;
import com.j256.ormlite.dao.Dao;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.misc.util.DateUtil;
import org.sr3u.photoframe.server.data.Item;
import sr3u.streamz.functionals.Consumerex;

import java.util.Date;

@Getter
public class Event {

    private static final Logger log = LogManager.getLogger(Event.class);

    Date eventQueryTimestamp;
    Item item;
    Date createdAt;
    PhotosLibraryClient gClient;
    Dao<Item, String> dao;
    MediaItem mediaItem;

    public void refreshMediaItem() {
        if (mediaItem == null || isMediaItemExpired()) {
            mediaItem = gClient.getMediaItem(item.getGoogleID());
            log.info("MediaItem refreshed");
        }
        eventQueryTimestamp = new Date();
    }

    private boolean isMediaItemExpired() {
        return DateUtil.isDateExpiredForMediaItem(eventQueryTimestamp);
    }

    public MediaItem getMediaItem() {
        refreshMediaItem();
        return mediaItem;
    }

    public Event(Date eventQueryTimestamp, Item item, MediaItem mediaItem, PhotosLibraryClient gClient, Dao<Item, String> dao) {
        this.eventQueryTimestamp = eventQueryTimestamp;
        this.item = item;
        this.mediaItem = mediaItem;
        this.gClient = gClient;
        this.dao = dao;
        createdAt = new Date();
    }

    public <ET extends Event> Event ifType(Class<ET> eventClazz, Consumerex<ET> handler) {
        if (eventClazz.isInstance(this)) {
            handler.wrap().accept(eventClazz.cast(this));
        }
        return this;
    }
}
