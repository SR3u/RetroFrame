package org.sr3u.photoframe.client.filters;

import lombok.Data;
import sr3u.streamz.functionals.BiFunctionex;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public interface ImageFilter extends BiFunctionex<ImageFilter.Context, Image, Image> {
    void reset();

    ImageFilter init(List<String> parameters);

    default ImageFilter init(String... parameters) {
        return init(Arrays.asList(parameters));
    }

    Image apply(Image image) throws Exception;

    @Override
    default Image apply(Context context, Image image) throws Exception {
        return apply(image);
    }

    @Data
    static class Context {
        private Dimension originalSize;
    }
}
