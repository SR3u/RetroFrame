package org.sr3u.retroframe.server.events;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.types.proto.MediaItem;
import com.j256.ormlite.dao.Dao;
import org.sr3u.retroframe.server.data.Item;

import java.util.Date;

public class RetrievedItemEvent extends Event {
    public RetrievedItemEvent(Date eventQueryTimestamp, Item item, MediaItem mediaItem, PhotosLibraryClient gClient, Dao<Item, String> dao) {
        super(eventQueryTimestamp, item, mediaItem, gClient, dao);
    }
}
