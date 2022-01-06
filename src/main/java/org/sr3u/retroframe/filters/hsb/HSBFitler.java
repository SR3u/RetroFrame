package org.sr3u.retroframe.filters.hsb;

import org.sr3u.retroframe.filters.FastImageFilter;
import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.utils.ArgParser;
import sr3u.streamz.functionals.primitive.doublefloat.DoubleFunctionex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public abstract class HSBFitler implements FastImageFilter {
    private double factor = 1.0;
    private final int hsbIdx;
    protected final String[] hsbChannelNames = new String[]{"hue", "saturation", "brightness"};

    protected final List<DoubleFunctionex<Float>> clip = Arrays.asList(
            i -> rotateAt(i, 1.0), i -> clipAt(i, 1.0), i -> clipAt(i, 1.0)
    );

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

    private void multiplyHSB(BufferedImage image, int x, int y, double factor) throws Exception {
        Color pixel = new Color(image.getRGB(x, y));
        float[] hsb = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
        hsb[hsbIdx] = clip.get(hsbIdx).apply(hsb[hsbIdx] * factor);
        image.setRGB(x, y, Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser argParser = new ArgParser(parameters);
        this.factor = argParser.doubleAt(0).orElse(this.factor);
        return this;
    }

    @Override
    public String toString() {
        return getClass().getName() + " (" + hsbChannelNames[hsbIdx] + " * " + factor + ")";
    }

    private float clipAt(double i, double max) {
        if (i > max) {
            return (float) max;
        }
        return (float) i;
    }

    private float rotateAt(double i, double max) {
        i -= ((int) i);
        if (i <= 0.0) {
            return 0.0f;
        }
        return (float) i;
    }

    @Override
    public void reset() {
    }

}
