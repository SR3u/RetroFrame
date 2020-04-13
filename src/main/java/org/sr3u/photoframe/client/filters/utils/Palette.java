package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.List;
import java.util.*;

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

    @Override
    public String toString() {
        return name;
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
}
