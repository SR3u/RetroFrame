package org.sr3u.photoframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.MediaItem;
import com.j256.ormlite.dao.Dao;
import org.sr3u.photoframe.server.data.Item;

public class NewItemEvent extends Event {
    public NewItemEvent(Item item, MediaItem mediaItem, PhotosLibraryClient gClient, Dao<Item, String> dao) {
        super(item, mediaItem, gClient, dao);
    }
}
