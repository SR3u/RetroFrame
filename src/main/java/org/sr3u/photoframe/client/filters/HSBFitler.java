package org.sr3u.photoframe.client.filters;

import org.sr3u.photoframe.client.filters.utils.ArgParser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class HSBFitler implements FastImageFilter {
    private double factor = 1.0;
    private final int hsbIdx;

    protected HSBFitler(int hsbIdx) {
        this.hsbIdx = hsbIdx;
    }

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        multiplyHSB(image, x, y, this.factor);
    }

    private void multiplyHSB(BufferedImage image, int x, int y, double factor) {
        Color pixel = new Color(image.getRGB(x, y));
        float[] hsb = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
        hsb[hsbIdx] *= factor;
        if (hsb[hsbIdx] > 1.0) {
            hsb[hsbIdx] = 1.0f;
        }
        image.setRGB(x, y, Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser argParser = new ArgParser(parameters);
        this.factor = argParser.doubleAt(0).orElse(this.factor);
        return this;
    }
}
