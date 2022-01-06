package org.sr3u.retroframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.j256.ormlite.dao.Dao;
import org.sr3u.retroframe.server.data.Item;

public class DeletedItemEvent extends Event {
    public DeletedItemEvent(Item item, PhotosLibraryClient gClient, Dao<Item, String> dao) {
        super(null, item, null, gClient, dao);
    }
}
