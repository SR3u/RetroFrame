package org.sr3u.photoframe.client.filters;

import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface FastImageFilter extends ImageFilter {

    void apply(BufferedImage image, int x, int y) throws Exception;

    @Override
    default Image apply(Image image) throws Exception {
        BufferedImage result = ImageUtil.bufferedCopy(image);
        for (int y = 0; y < image.getHeight(null); y++) {
            for (int x = 0; x < image.getWidth(null); x++) {
                apply(result, x, y);
            }
        }
        return result;
    }
}