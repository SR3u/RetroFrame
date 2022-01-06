/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

public class Sierra24A extends ErrDiffusionDither {

    public Sierra24A() {
        super(new double[][]{{2}, {1, 1}},
                4,
                1);
    }

    @Override
    public String toString() {
        return "Sierra Lite 2-4A Dither  " + paletteString();
    }
}
