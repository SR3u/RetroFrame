/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

/**
 * The Stucki Dithering algorithm.
 */
public class Stucki extends ErrDiffusionDither {

    public Stucki() {
        super(new double[][]{{8, 4}, {2, 4, 8, 4, 2}, {1, 2, 4, 2, 1}},
                42,
                2);
    }

    @Override
    public String toString() {
        return "Stucki Dither  " + paletteString();
    }

}
