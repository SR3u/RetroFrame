/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package org.sr3u.retroframe.filters.dither;

import org.sr3u.retroframe.filters.FastImageFilter;

/**
 * As there are many dithering algorithms, the Ditherer interface
 * provides a common way to speak to them.
 */
public interface Ditherer extends FastImageFilter {
}
