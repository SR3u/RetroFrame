package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

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

    public static Palette parse(String name, List<String> args) {
        if (args.size() > 0) {
            Palette palette = Palette.get(args.get(0));
            if (palette == null) {
                Palette.ColorPicker colorPicker = args.stream()
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
        this.palette = palette;
        this.colorPicker = colorPicker;
        if (name != null) {
            ALL.put(name.toLowerCase(), this);
        }
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
        return colorPicker.closestColor(c, palette);
    }

    public static ColorPicker getColorPicker(String s) {
        String param = s == null ? null : s.toLowerCase();
        return Optional.ofNullable(ALL_PICKERS.get(param)).orElseGet(BruteForcePicker::new);
    }

    public interface ColorPicker {
        Color closestColor(Color c, Color[] palette);
    }

    public static class LuminancePicker extends BruteForcePicker {

        @Override
        public double distance(final Color a, final Color b) {
            final double lumadiff = luminanceDistance(a, b);
            final double diffR = (a.getRed() - b.getRed());
            final double diffG = (a.getGreen() - b.getGreen());
            final double diffB = (a.getBlue() - b.getBlue());
            return (diffR * diffR * 0.299 + diffG * diffG * 0.587 + diffB * diffB * 0.114) * 0.75
                    + lumadiff * lumadiff;
        }

        public static double luminanceDistance(Color a, Color b) {
            // Compare the difference of two RGB values, weigh by CCIR 601 luminosity:
            final double luma1 = (a.getRed() * 299 + a.getGreen() * 587 + a.getBlue() * 114) / 1000.0;
            final double luma2 = (b.getRed() * 299 + b.getGreen() * 587 + b.getBlue() * 114) / 1000.0;
            return luma1 - luma2;
        }
    }

    public static class BruteForcePicker implements ColorPicker {

        @Override
        public Color closestColor(Color c, Color[] palette) {
            Color closest = palette[0];

            for (Color n : palette) {
                if (distance(n, c) < distance(closest, c)) {
                    closest = n;
                }
            }
            return closest;
        }

        protected double distance(Color c1, Color c2) {
            return squareDistance(c1, c2);
        }

        public static int squareDistance(Color c1, Color c2) {
            int Rdiff = c1.getRed() - c2.getRed();
            int Gdiff = c1.getGreen() - c2.getBlue();
            int Bdiff = c1.getGreen() - c2.getBlue();
            return Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
        }

        public static double normalizedDistance(Color c1, Color c2) {
            return squareDistance(c1, c2) / 195075.0;
        }
    }
}
