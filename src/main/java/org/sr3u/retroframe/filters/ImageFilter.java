package org.sr3u.retroframe.filters;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
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

    default Info getInfo() {
        return Info.builder().build();
    }

    @Data
    class Context {
        private Dimension originalSize;
    }

    @Value
    @Builder
    class Info {
        @Builder.Default
        boolean paletteArgument = false;
    }
}
