package org.sr3u.retroframe.filters.rgb;

public class GreenChannel extends RgbFilter {
    public GreenChannel() {
        super(c -> {
            float[] rgbComponents = c.getRGBComponents(null);
            rgbComponents[0] = 0;
            rgbComponents[2] = 0;
            return colorFromRGBA(rgbComponents);
        });
    }
}
