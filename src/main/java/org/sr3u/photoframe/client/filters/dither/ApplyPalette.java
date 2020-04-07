/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.photoframe.client.filters.dither;

import org.sr3u.photoframe.client.filters.utils.Palette;
import org.sr3u.photoframe.client.filters.utils.PaletteParser;
import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * Just replaces each pixel with the nearest match,
 * ignoring any error accumulation that a dither would do.
 *
 * @see PaletteParser
 * @see Palette
 */
public final class ApplyPalette implements Ditherer, PaletteParser {

    private Palette palette;

    @Override
    public java.awt.Image dither(java.awt.Image input) {
        final BufferedImage output = ImageUtil.bufferedCopy(input);
        final int width = output.getWidth();
        final int height = output.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                output.setRGB(x, y, palette.closestColor(output.getRGB(x, y)).getRGB());
            }
        }

        return output;
    }

    @Override
    public String toString() {
        return "No Dither";
    }

    @Override
    public void setPalette(Palette palette) {
        this.palette = palette;
    }
}
