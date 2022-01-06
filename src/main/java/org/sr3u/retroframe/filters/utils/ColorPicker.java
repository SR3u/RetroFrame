package org.sr3u.retroframe.filters.utils;

import java.awt.*;

public interface ColorPicker {

    default Color closestColor(Color c, Color[] palette) {
        return closestColor(c.getRGB(), palette);
    }

    Color closestColor(int rgb, Color[] palette);

    double distance(Color a, Color b);

    void reset();

    Color cachedColor(int rgb);

    String getName();
}
