/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

/**
 * The Atkinson Error-diffusion algorithm
 *
 * @author Richard Todd
 */
public class Atkinson extends ErrDiffusionDither {

    public Atkinson() {
        super(new double[][]{{1, 1}, {1, 1, 1}, {0, 1}},
                8,
                1);
    }

    @Override
    public String toString() {
        return "Atkinson Dither " + paletteString();
    }
}
