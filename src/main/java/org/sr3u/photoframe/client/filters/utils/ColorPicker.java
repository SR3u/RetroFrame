package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;

public interface ColorPicker {
    Color closestColor(Color c, Color[] palette);
}
