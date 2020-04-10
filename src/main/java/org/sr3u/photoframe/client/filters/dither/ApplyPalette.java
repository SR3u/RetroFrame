/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.photoframe.client.filters.dither;

import org.sr3u.photoframe.client.filters.utils.Palette;
import org.sr3u.photoframe.client.filters.utils.PaletteParser;

import java.awt.image.BufferedImage;

/**
 * Just replaces each pixel with the nearest match,
 * ignoring any error accumulation that a dither would do.
 *
 * @see PaletteParser
 * @see Palette
 */
public final class ApplyPalette implements Ditherer, PaletteParser {

    private Palette palette = Palette.DEFAULT;

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        image.setRGB(x, y, palette.closestColor(image.getRGB(x, y)).getRGB());
    }

    @Override
    public String toString() {
        return "No Dither";
    }

    @Override
    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    @Override
    public void reset() {
        this.palette.reset();
    }
}
