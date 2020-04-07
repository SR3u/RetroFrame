package org.sr3u.photoframe.client.filters.utils;

import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.filters.dither.ErrDiffusionDither;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
 * @see Palette.ColorPicker
 * @see ErrDiffusionDither
 */
public interface PaletteParser extends ImageFilter {
    @Override
    default ImageFilter init(List<String> args) {
        if (args.size() > 0) {
            Palette palette = Palette.get(args.get(0));
            if (palette == null) {
                Palette.ColorPicker colorPicker = args.stream()
                        .filter(Objects::nonNull)
                        .filter(s -> !s.startsWith("#"))
                        .map(Palette::getColorPicker)
                        .findFirst().orElseThrow();
                palette = new Palette("custom", colorPicker, args.stream()
                        .filter(Objects::nonNull)
                        .filter(s -> s.startsWith("#"))
                        .map(Color::decode)
                        .collect(Collectors.toSet()));
            }
            this.setPalette(palette);
        } else {
            this.setPalette(Palette.BNW);
        }
        return this;
    }

    void setPalette(Palette palette);
}
