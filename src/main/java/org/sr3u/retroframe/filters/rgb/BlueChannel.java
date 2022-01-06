package org.sr3u.retroframe.filters.rgb;

public class BlueChannel extends RgbFilter {
    public BlueChannel() {
        super(c -> {
            float[] rgbComponents = c.getRGBComponents(null);
            rgbComponents[0] = 0;
            rgbComponents[1] = 0;
            return colorFromRGBA(rgbComponents);
        });
    }
}
