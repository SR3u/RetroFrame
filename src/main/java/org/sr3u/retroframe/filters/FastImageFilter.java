package org.sr3u.retroframe.filters;

import org.sr3u.retroframe.filters.utils.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface FastImageFilter extends ImageFilter {

    Object createContext(BufferedImage image);

    void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception;

    @Override
    default Image apply(Image image) throws Exception {
        BufferedImage result = ImageUtil.buffer(image);
        Object context = createContext(result);
        for (int y = 0; y < image.getHeight(null); y++) {
            for (int x = 0; x < image.getWidth(null); x++) {
                apply(result, context, x, y);
            }
        }
        return result;
    }
}