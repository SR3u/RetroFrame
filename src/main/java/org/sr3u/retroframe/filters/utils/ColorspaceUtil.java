package org.sr3u.retroframe.filters.utils;

import java.awt.*;

public class ColorspaceUtil {
    public static final int CYAN_INDEX = 0;
    public static final int MAGENTA_INDEX = 1;
    public static final int YELLOW_INDEX = 2;
    public static final int BLACK_INDEX = 3;

    public static float[] cmyk(float... rgb) {
        return cmyk(rgb[0], rgb[1], rgb[2]);
    }

    public static Color rgbColor(float[] rgb) {
        return new Color(
                (int) (rgb[0] * 255),
                (int) (rgb[1] * 255),
                (int) (rgb[2] * 255));
    }

    public static float[] rgb(float... cmyk) {
        if (cmyk.length < 4) {
            return rgb(cmyk[CYAN_INDEX], cmyk[MAGENTA_INDEX], cmyk[YELLOW_INDEX]);
        }
        return rgb(cmyk[CYAN_INDEX], cmyk[MAGENTA_INDEX], cmyk[YELLOW_INDEX], cmyk[BLACK_INDEX]);
    }


    public static float[] cmyk(int r, int g, int b) {
        return cmyk(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static float[] cmyk(float r, float g, float b) {
        float k = 1 - Math.max(r, b);
        float c = (1 - r - k) / (1 - k);
        float m = (1 - g - k) / (1 - k);
        float y = (1 - b - k) / (1 - k);
        return new float[]{c, m, y, k};
    }

    public static float[] cmy(int r, int g, int b) {
        return cmy(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static float[] cmy(float... rgb) {
        return new float[]{1.0f - rgb[0], 1.0f - rgb[1], 1.0f - rgb[2]};
    }

    public static float[] rgb(float c, float m, float y) {
        return new float[]{1f - c, 1f - m, 1f - y};
    }

    public static float[] rgb(float c, float m, float y, float k) {
        float r = clamp((1 - c) * (1 - k));
        float g = clamp((1 - m) * (1 - k));
        float b = clamp((1 - y) * (1 - k));
        return new float[]{r, g, b};
    }

    public static float clamp(float v) {
        return Math.min(Math.max(v, 0), 1);
    }
}
