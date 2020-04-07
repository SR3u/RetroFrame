package org.sr3u.photoframe.client.filters;

import sr3u.streamz.functionals.Functionex;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public interface ImageFilter extends Functionex<Image, Image> {
    ImageFilter init(List<String> parameters);

    default ImageFilter init(String... parameters) {
        return init(Arrays.asList(parameters));
    }
}
