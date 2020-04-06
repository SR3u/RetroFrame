package org.sr3u.photoframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.MediaItem;
import com.j256.ormlite.dao.Dao;
import org.sr3u.photoframe.server.data.Item;

import java.util.Date;

public class NewItemEvent extends Event {
    public NewItemEvent(Date eventQueryTimestamp, Item item, MediaItem mediaItem, PhotosLibraryClient gClient, Dao<Item, String> dao) {
        super(eventQueryTimestamp, item, mediaItem, gClient, dao);
    }
}
