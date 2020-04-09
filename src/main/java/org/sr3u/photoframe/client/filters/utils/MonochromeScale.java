package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.ArrayList;

public class MonochromeScale extends Palette {
    public MonochromeScale(String name, ColorPicker colorPicker, Color baseColor) {
        super(name, colorPicker, createColors(baseColor));
    }

    private static java.util.List<Color> createColors(Color baseColor) {
        java.util.List<Color> colors = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            double factor = i / 255.0;
            colors.add(createColor(baseColor, factor));
        }
        return colors;
    }

    private static Color createColor(Color baseColor, double brightness) {
        float[] hsb = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
        hsb[2] = clipAt(hsb[2] * brightness, 1.0);
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    private static float clipAt(double i, double max) {
        if (i > max) {
            return (float) max;
        }
        return (float) i;
    }
}
