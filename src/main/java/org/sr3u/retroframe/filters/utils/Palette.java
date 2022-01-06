package org.sr3u.retroframe.filters.utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Palette {
    private static final Map<String, Palette> ALL = new HashMap<>();
    private static final Map<String, ColorPicker> ALL_PICKERS = new HashMap<>();

    static {
        ALL_PICKERS.put("luminance", new LuminancePicker());
        ALL_PICKERS.put("default", new BruteForcePicker());
        ALL_PICKERS.put("bruteforce", new BruteForcePicker());
        ALL_PICKERS.put("brute", new BruteForcePicker());
    }

    protected String name;

    protected Palette(String name) {
        this.name = name;
        if (name != null) {
            ALL.put(name.toLowerCase(), this);
        }
    }

    public static Palette parse(String name, String paletteString) {
        return parse(name, Arrays.asList(paletteString.split(" ")));
    }

    public static List<String> allNames() {
        return ALL.keySet().stream().sorted().collect(Collectors.toList());
    }

    public static boolean isValid(String selectedItem) {
        Palette parse = parse(null, selectedItem);
        if (parse instanceof PredefinedPalette) {
            return parse.toPredefined().getColors().length > 0;
        }
        return parse != null;
    }

    public Palette append(Palette... palettes) {
        return new MultiPalette(this).append(palettes);
    }

    protected abstract PredefinedPalette toPredefined();

    public static Palette parse(String name, List<String> args) {
        if (args.size() > 0) {
            Palette palette = Palette.get(args.get(0));
            if (palette == null) {
                return PredefinedPalette.parsePredefined(name, args);
            }
            return palette;
        } else {
            return Palette.defaultPalette();
        }
    }

    public static Palette get(String name) {
        String param = name == null ? null : name.toLowerCase();
        return ALL.get(param);
    }

    public Color closestColor(int argb) {
        return closestColor(new Color(argb));
    }

    public abstract Color closestColor(Color c);

    public static ColorPicker getColorPicker(String s) {
        String param = s == null ? null : s.toLowerCase();
        return Optional.ofNullable(ALL_PICKERS.get(param)).orElseGet(BruteForcePicker::new);
    }

    public void reset() {
    }

    public static Palette defaultPalette() {
        return DefinedPalettes.BNW;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return super.toString();
    }
}
