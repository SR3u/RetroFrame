package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;

public class BruteForcePicker implements ColorPicker {

    @Override
    public Color closestColor(Color c, Color[] palette) {
        Color closest = palette[0];

        for (Color n : palette) {
            if (distance(n, c) < distance(closest, c)) {
                closest = n;
            }
        }
        return closest;
    }

    protected double distance(Color c1, Color c2) {
        return squareDistance(c1, c2);
    }

    public static int squareDistance(Color c1, Color c2) {
        int Rdiff = c1.getRed() - c2.getRed();
        int Gdiff = c1.getGreen() - c2.getBlue();
        int Bdiff = c1.getGreen() - c2.getBlue();
        return Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
    }

    public static double normalizedDistance(Color c1, Color c2) {
        return squareDistance(c1, c2) / 195075.0;
    }
}
