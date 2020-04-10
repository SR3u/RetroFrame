package org.sr3u.photoframe.client.filters;

import org.sr3u.photoframe.client.filters.rgb.BlueChannel;
import org.sr3u.photoframe.client.filters.rgb.GreenChannel;
import org.sr3u.photoframe.client.filters.rgb.RedChannel;

import java.awt.image.BufferedImage;
import java.util.List;

public class Trinitron implements FastImageFilter {

    private FastImageFilter[] scanlineFilters = new FastImageFilter[]{
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
        scanlineFilters[x % scanlineFilters.length].apply(image, contextObject, x, y);
    }

    @Override
    public void reset() {
        for (int i = 0; i < scanlineFilters.length; i++) {
            scanlineFilters[i].reset();
        }
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    private class Context {
        private final Object[] contexts;

        public Context(BufferedImage image, FastImageFilter[] scanlineFilters) {
            contexts = new Object[scanlineFilters.length];
            for (int i = 0; i < scanlineFilters.length; i++) {
                contexts[i] = scanlineFilters[i].createContext(image);
            }
        }
    }
}
