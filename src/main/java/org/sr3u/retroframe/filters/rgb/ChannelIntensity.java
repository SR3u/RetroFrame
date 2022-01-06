package org.sr3u.retroframe.filters.rgb;

import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.utils.ArgParser;

import java.awt.*;
import java.util.List;


public class ChannelIntensity extends RgbFilter {

    private float[] channelIntensityMask = new float[]{1.0f, 1.0f, 1.0f, 1.0f};

    public ChannelIntensity() {
        super(null);
        setColorMapper(this::maskColor);
    }

    public Color maskColor(Color c) {
        float[] rgbComponents = c.getRGBComponents(null);
        for (int i = 0; i < rgbComponents.length; i++) {
            rgbComponents[i] = Math.min(rgbComponents[i] * channelIntensityMask[i], 1.0f);
        }
        return colorFromRGBA(rgbComponents);
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser param = new ArgParser(parameters);
        for (int i = 0; i < channelIntensityMask.length; i++) {
            channelIntensityMask[i] = (float) param.doubleAt(i).orElse(channelIntensityMask[i]);
        }
        return super.init(parameters);
    }
}
