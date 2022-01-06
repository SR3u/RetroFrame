package org.sr3u.retroframe.filters.utils;

import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.dither.ErrDiffusionDither;

import java.util.List;

/**
 * A class to parse palette from input arguments list
 * <p>
 * returns either a palette by name, or palette of hex coded colors, #000000 to #ffffff
 * and a first found ColorPicker (or a default one if none found)
 * examples:
 * monochrome
 * cga1
 * LUMINANCE #000000 #111111 #222222 #444444 #666666 #777777 #888888 #AAAAAA #CCCCCC #EEEEEE #FFFFFF
 *
 * @see Palette
 * @see ColorPicker
 * @see ErrDiffusionDither
 */
public interface PaletteParser extends ImageFilter {
    @Override
    default ImageFilter init(List<String> args) {
        Palette custom = Palette.parse("custom", args);
        if (custom != null) {
            this.setPalette(custom);
        } else {
            this.setPalette(Palette.defaultPalette());
        }
        return this;
    }

    void setPalette(Palette palette);

    @Override
    default ImageFilter.Info getInfo() {
        return Info.builder()
                .paletteArgument(true)
                .build();
    }

    String paletteString();
}
