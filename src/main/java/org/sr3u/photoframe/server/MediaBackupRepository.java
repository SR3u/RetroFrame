package org.sr3u.photoframe.server;

import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.data.Item;

import java.awt.*;

public interface MediaBackupRepository {
    ImageWithMetadata getItem(Dimension size, Item item);
}
