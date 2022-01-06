package org.sr3u.retroframe.filters.utils;

import java.awt.*;
import java.util.ArrayList;

public class MonochromeScale extends Palette {
    private static final int HUE_IDX = 0;
    private static final int SATURATION_IDX = 1;
    private static final int BRIGHTNESS_IDX = 2;

    private final int levels;
    private final Color baseColor;
    private final float[] baseHsb;

    public MonochromeScale(String name, Color baseColor, int levels) {
        super(name);
        this.baseColor = baseColor;
        this.levels = levels;
        this.baseHsb = hsb(baseColor);
    }

    private static java.util.List<Color> createColors(Color baseColor, int levels) {
        java.util.List<Color> colors = new ArrayList<>(levels);
        for (int i = 0; i <= levels; i++) {
            double factor = i / (1.0 * levels);
            colors.add(createColor(baseColor, factor));
        }
        return colors;
    }

    private static Color createColor(Color baseColor, double brightness) {
        float[] hsb = hsb(baseColor);
        hsb[BRIGHTNESS_IDX] = clipAt(hsb[BRIGHTNESS_IDX] * brightness, 1.0);
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[BRIGHTNESS_IDX]));
    }

    private static float[] hsb(Color baseColor) {
        return Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
    }

    private static float clipAt(double i, double max) {
        if (i > max) {
            return (float) max;
        }
        return (float) i;
    }

    @Override
    protected PredefinedPalette toPredefined() {
        return new PredefinedPalette(null, new LuminancePicker(), createColors(baseColor, levels));
    }

    @Override
    public Color closestColor(Color c) {
        float[] hsb = hsb(c);
        double brightness = hsb[BRIGHTNESS_IDX];
        hsb[BRIGHTNESS_IDX] = clipAt(baseHsb[BRIGHTNESS_IDX] * brightness, 1.0);
        return new Color(Color.HSBtoRGB(baseHsb[HUE_IDX], hsb[SATURATION_IDX], hsb[BRIGHTNESS_IDX]));
    }

}
