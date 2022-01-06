package org.sr3u.retroframe.filters.rgb;

import org.sr3u.retroframe.filters.FastImageFilter;
import org.sr3u.retroframe.filters.ImageFilter;
import sr3u.streamz.functionals.Functionex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class RgbFilter implements FastImageFilter {

    private Functionex<Color, Color> colorMapper;

    protected RgbFilter(Functionex<Color, Color> colorMapper) {
        this.colorMapper = colorMapper;
    }

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        Color newPixel = colorMapper.apply(new Color(image.getRGB(x, y)));
        image.setRGB(x, y, newPixel.getRGB());
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    public static Color colorFromRGBA(float[] rgba) {
        if (rgba.length < 3) {
            throw new RuntimeException("Expected 3 arguments array");
        }
        if (rgba.length > 3) {
            return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
        } else {
            return new Color(rgba[0], rgba[1], rgba[2]);
        }
    }

    protected void setColorMapper(Functionex<Color, Color> colorMapper) {
        this.colorMapper = colorMapper;
    }

    @Override
    public void reset() {
    }
}
