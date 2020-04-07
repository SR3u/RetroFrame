package org.sr3u.photoframe.client.filters.dither;

import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.filters.utils.Palette;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
