package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Palette {
    private static final Map<String, Palette> ALL = new HashMap<>();
    private static final Map<String, ColorPicker> ALL_PICKERS = new HashMap<>();
    public static final Palette DEFAULT = DefinedPalettes.BNW;

    static {
        ALL_PICKERS.put("luminance", new LuminancePicker());
        ALL_PICKERS.put("default", new BruteForcePicker());
        ALL_PICKERS.put("bruteforce", new BruteForcePicker());
        ALL_PICKERS.put("brute", new BruteForcePicker());
    }

    private String name;
    private Color[] palette;
    private ColorPicker colorPicker;

    public Palette(String name, Collection<Color> colors) {
        this(name, colors.toArray(new Color[0]));
    }

    public Palette(String name, ColorPicker colorPicker, Collection<Color> colors) {
        this(name, colorPicker, colors.toArray(new Color[0]));
    }

    public Palette(String name, ColorPicker colorPicker, Palette prototype) {
        this(name, colorPicker, prototype.palette);
    }

    public static Palette parse(String name, String paletteString) {
        return parse(name, Arrays.asList(paletteString.split(" ")));
    }

    public static Palette merge(String name, Palette a, Palette b) {
        return new Palette(name, mergeColors(a, b));
    }

    public static Palette merge(String name, ColorPicker colorPicker, Palette a, Palette b) {
        return new Palette(name, colorPicker, mergeColors(a, b));
    }

    private static List<Color> mergeColors(Palette a, Palette b) {
        return Stream.concat(Arrays.stream(a.palette), Arrays.stream(b.palette))
                .distinct()
                .collect(Collectors.toList());
    }

    public static Palette parse(String name, List<String> args) {
        if (args.size() > 0) {
            Palette palette = Palette.get(args.get(0));
            if (palette == null) {
                ColorPicker colorPicker = args.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .filter(s -> !s.startsWith("#"))
                        .map(Palette::getColorPicker)
                        .findFirst().orElseGet(BruteForcePicker::new);
                palette = new Palette(name, colorPicker, args.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .filter(s -> s.startsWith("#"))
                        .map(Color::decode)
                        .collect(Collectors.toSet()));
            }
            return palette;
        } else {
            return Palette.DEFAULT;
        }
    }

    public static Palette get(String name) {
        String param = name == null ? null : name.toLowerCase();
        return ALL.get(param);
    }

    public Palette(String name, Palette p) {
        this(name, p.colorPicker, p.palette);
    }

    public Palette(String name, ColorPicker colorPicker, Color... palette) {
        this.name = name;
        this.palette = filterDuplicates(palette);
        this.colorPicker = colorPicker;
        if (name != null) {
            ALL.put(name.toLowerCase(), this);
        }
    }

    private Color[] filterDuplicates(Color[] palette) {
        return Arrays.stream(palette)
                .mapToInt(Color::getRGB)
                .distinct()
                .mapToObj(Color::new)
                .toArray(Color[]::new);
    }

    public Palette(String name, Color... palette) {
        this(name, new BruteForcePicker(), palette);
    }

    @Override
    public String toString() {
        return name;
    }

    public Color closestColor(int argb) {
        return closestColor(new Color(argb));
    }

    public Color closestColor(Color c) {
        return colorPicker.closestColor(c.getRGB(), palette);
    }

    public static ColorPicker getColorPicker(String s) {
        String param = s == null ? null : s.toLowerCase();
        return Optional.ofNullable(ALL_PICKERS.get(param)).orElseGet(BruteForcePicker::new);
    }

    public void reset() {
        colorPicker.reset();
    }
}
