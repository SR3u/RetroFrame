package org.sr3u.photoframe.client.filters;

import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.*;

public class Buffer implements ImageFilter {
    @Override
    public ImageFilter init(java.util.List<String> parameters) {
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        return ImageUtil.buffer(image);
    }
}
