package org.sr3u.retroframe.filters.utils;

import sr3u.streamz.streams.Streamex;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredefinedPalette extends Palette {
    private Color[] palette;
    private ColorPicker colorPicker;

    public PredefinedPalette(String name, Collection<Color> colors) {
        this(name, colors.toArray(new Color[0]));
    }

    public PredefinedPalette(String name, ColorPicker colorPicker, Collection<Color> colors) {
        this(name, colorPicker, colors.toArray(new Color[0]));
    }

    public PredefinedPalette(String name, ColorPicker colorPicker, PredefinedPalette prototype) {
        this(name, colorPicker, prototype.palette);
    }

    public PredefinedPalette(String name, PredefinedPalette p) {
        this(name, p.colorPicker, p.palette);
    }

    public PredefinedPalette(String name, Color... palette) {
        this(name, new BruteForcePicker(), palette);
    }

    public PredefinedPalette(String name, ColorPicker colorPicker, Color... palette) {
        super(name);
        this.palette = filterDuplicates(palette);
        this.colorPicker = colorPicker;
    }

    public static PredefinedPalette parsePredefined(String name, List<String> args) {
        ColorPicker colorPicker = args.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.startsWith("#"))
                .map(Palette::getColorPicker)
                .findFirst().orElseGet(BruteForcePicker::new);
        return new PredefinedPalette(name, colorPicker, args.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> s.startsWith("#"))
                .map(Color::decode)
                .collect(Collectors.toSet()));
    }

    private static List<Color> mergeColors(PredefinedPalette a, PredefinedPalette b) {
        return Stream.concat(Arrays.stream(a.palette), Arrays.stream(b.palette))
                .distinct()
                .collect(Collectors.toList());
    }

    protected Color[] filterDuplicates(Color[] palette) {
        return Arrays.stream(palette)
                .mapToInt(Color::getRGB)
                .distinct()
                .mapToObj(Color::new)
                .toArray(Color[]::new);
    }

    @Override
    public Color closestColor(Color c) {
        return colorPicker.closestColor(c.getRGB(), palette);
    }

    @Override
    public void reset() {
        super.reset();
        colorPicker.reset();
    }

    @Override
    public Palette append(Palette... palettes) {
        return this;
    }

    @Override
    protected PredefinedPalette toPredefined() {
        return this;
    }

    protected Color[] getColors() {
        return palette;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return "Palette " + " " + colorPicker.getName() + " " + Streamex.of(getColors())
                .mapToString(c -> String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()))
                .joined(" ");
    }
}
