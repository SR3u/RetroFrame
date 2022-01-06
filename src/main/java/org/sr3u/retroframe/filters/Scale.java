package org.sr3u.retroframe.filters;

import org.sr3u.retroframe.filters.utils.ArgParser;
import sr3u.streamz.optionals.OptionalDoublex;

import java.awt.*;
import java.util.List;

public class Scale implements ImageFilter {
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser parser = new ArgParser(parameters);
        OptionalDoublex oScale = parser.doubleAt(0);
        scaleX = oScale.orElse(scaleX);
        OptionalDoublex oScaleY = parser.doubleAt(1);
        if (!oScaleY.isPresent()) {
            scaleY = oScale.orElse(scaleX);
        }
        scaleY = oScaleY.orElse(scaleY);
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        int newWidth = (int) (image.getWidth(null) * scaleX);
        int newHeight = (int) (image.getHeight(null) * scaleY);
        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    @Override
    public void reset() {
    }
}
