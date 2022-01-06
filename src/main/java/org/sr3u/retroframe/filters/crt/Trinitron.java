package org.sr3u.retroframe.filters.crt;

import org.sr3u.retroframe.filters.FastImageFilter;
import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.rgb.BlueChannel;
import org.sr3u.retroframe.filters.rgb.GreenChannel;
import org.sr3u.retroframe.filters.rgb.RedChannel;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public abstract class Trinitron implements FastImageFilter {

    protected FastImageFilter[] scanlineFilters = new FastImageFilter[]{
            new RedChannel(),
            new GreenChannel(),
            new BlueChannel()
    };

    @Override
    public Object createContext(BufferedImage image) {
        return new Context(image, scanlineFilters);
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        scanlineFilters[getIndex(x, y)].apply(image, contextObject, x, y);
    }

    protected abstract int getIndex(int x, int y);

    @Override
    public void reset() {
        Arrays.stream(scanlineFilters).forEach(ImageFilter::reset);
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    private static class Context {
        private final Object[] contexts;

        public Context(BufferedImage image, FastImageFilter[] scanlineFilters) {
            contexts = new Object[scanlineFilters.length];
            for (int i = 0; i < scanlineFilters.length; i++) {
                contexts[i] = scanlineFilters[i].createContext(image);
            }
        }
    }
}
