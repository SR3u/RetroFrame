package org.sr3u.retroframe.filters;

import java.awt.*;

public class OriginalSize extends Resize {
    @Override
    public Image apply(ImageFilter.Context context, Image image) throws Exception {
        this.height = (int) context.getOriginalSize().getHeight();
        this.width = (int) context.getOriginalSize().getWidth();
        return apply(image);
    }
}
