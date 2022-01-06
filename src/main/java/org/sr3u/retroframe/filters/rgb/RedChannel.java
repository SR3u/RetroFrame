package org.sr3u.retroframe.filters.rgb;

public class RedChannel extends RgbFilter {
    public RedChannel() {
        super(c -> {
            float[] rgbComponents = c.getRGBComponents(null);
            rgbComponents[1] = 0;
            rgbComponents[2] = 0;
            return colorFromRGBA(rgbComponents);
        });
    }
}
