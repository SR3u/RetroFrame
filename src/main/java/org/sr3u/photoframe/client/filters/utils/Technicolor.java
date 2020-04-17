package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;

import static org.sr3u.photoframe.client.filters.utils.ColorspaceUtil.*;

public class Technicolor extends Palette {

    private Palette orangeRed = new MonochromeScale(null, Color.decode("#FF00AB"), 256);
    private Palette cyanGreen = new MonochromeScale(null, Color.decode("#00FF84"), 256);

    protected Technicolor(String name) {
        super(name);
    }

    @Override
    protected PredefinedPalette toPredefined() {
        PredefinedPalette result = new PredefinedPalette(null, orangeRed.toPredefined());
        result.append(cyanGreen.toPredefined());
        return result;
    }

    @Override
    public Color closestColor(Color c) {
        // see https://pavel-kosenko.livejournal.com/18754.html
        float[] cmy1 = cmy(c.getRed(), c.getGreen(), c.getBlue());
        float[] cmy2 = cmy(c.getRed(), c.getGreen(), c.getBlue());

        cmy1[CYAN_INDEX] = cmy1[MAGENTA_INDEX];
        cmy1[MAGENTA_INDEX] = cmy1[YELLOW_INDEX];
        cmy1[YELLOW_INDEX] = 0;
        cmy2[MAGENTA_INDEX] = 0;

        float[] cmyR = new float[]{
                Math.min(cmy1[CYAN_INDEX] + cmy2[CYAN_INDEX], 1),
                Math.min(cmy1[MAGENTA_INDEX] + cmy2[MAGENTA_INDEX], 1),
                Math.min(cmy1[YELLOW_INDEX] + cmy2[YELLOW_INDEX], 1)
        };
        float[] rgb = rgb(cmyR);
        return rgbColor(rgb);
    }

}
