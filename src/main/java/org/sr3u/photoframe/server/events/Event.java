package org.sr3u.photoframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.MediaItem;
import com.j256.ormlite.dao.Dao;
import lombok.Getter;
import org.sr3u.photoframe.server.Server;
import org.sr3u.photoframe.server.data.Item;
import sr3u.streamz.functionals.Consumerex;

import java.util.Date;

@Getter
public class Event {

    Date eventQueryTimestamp;
    Item item;
    Date createdAt;
    PhotosLibraryClient gClient;
    Dao<Item, String> dao;
    MediaItem mediaItem;

    public void refreshMediaItem() {
        if (mediaItem == null || isMediaItemExpired(new Date())) {
            mediaItem = gClient.getMediaItem(item.getGoogleID());
            System.out.println("MediaItem refreshed");
        }
        eventQueryTimestamp = new Date();
    }

    private boolean isMediaItemExpired(Date now) {
        return eventQueryTimestamp == null || (((now.getTime() - eventQueryTimestamp.getTime()) / 1000) > Server.settings.getMedia().mediaItemExpiryTime);
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
