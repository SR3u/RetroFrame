/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.photoframe.client.filters.dither;

import org.sr3u.photoframe.client.filters.ImageFilter;

import java.awt.*;

/**
 * As there are many dithering algorithms, the Ditherer interface
 * provides a common way to speak to them.
 *
 * @author Richard Todd
 */
public interface Ditherer extends ImageFilter {
    Image dither(Image input);

    @Override
    default Image apply(Image image) throws Exception {
        return dither(image);
    }
}
