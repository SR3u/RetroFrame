package org.sr3u.retroframe.server;

import org.sr3u.retroframe.server.data.ImageWithMetadata;
import org.sr3u.retroframe.server.data.Item;

import java.awt.*;

public interface MediaBackupRepository {
    ImageWithMetadata getItem(Dimension size, Item item);
}
