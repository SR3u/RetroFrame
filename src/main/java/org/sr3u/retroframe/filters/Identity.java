package org.sr3u.retroframe.filters;

import java.awt.*;

public class Identity implements ImageFilter {
    @Override
    public ImageFilter init(java.util.List<String> parameters) {
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        return image;
    }

    @Override
    public void reset() {
    }
}
