package org.sr3u.photoframe.client.filters;

import org.sr3u.photoframe.client.filters.utils.ArgParser;
import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.*;
import java.util.List;

public class Resize implements ImageFilter {
    int width = -1;
    int height = -1;

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser parser = new ArgParser(parameters);
        width = parser.intAt(0).orElse(width);
        height = parser.intAt(1).orElse(height);
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        int imageWidth = image.getWidth(null);
        if (this.width == -1) {
            this.width = imageWidth;
        }
        int imageHeight = image.getHeight(null);
        if (this.height == -1) {
            this.height = imageHeight;
        }
        return ImageUtil.scaledImage(image, this.width, this.height);
    }
}
