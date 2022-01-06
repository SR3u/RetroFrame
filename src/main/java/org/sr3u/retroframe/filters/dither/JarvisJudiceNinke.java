/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

/**
 * The Jarvice, Judice, and Ninke Dithering algorithm.
 */
public class JarvisJudiceNinke extends ErrDiffusionDither {

    public JarvisJudiceNinke() {
        super(new double[][]{{7, 5}, {3, 5, 7, 5, 3}, {1, 3, 5, 3, 1}},
                48,
                2);
    }

    @Override
    public String toString() {
        return "Jarvis Judice Ninke Dither " + paletteString();
    }

}
