/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

/**
 * The Floyd-Steinberg Error-diffusion algorithm
 */
public class FloydSteinberg extends ErrDiffusionDither {

    public FloydSteinberg() {
        super(new double[][]{{7}, {3, 5, 1}},
                16,
                1);
    }

    @Override
    public String toString() {
        return "Floyd Steinberg Dither " + paletteString();
    }
}
