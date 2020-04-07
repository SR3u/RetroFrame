package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Palette {
    private static final Map<String, Palette> ALL = new HashMap<>();
    private static final Map<String, ColorPicker> ALL_PICKERS = new HashMap<>();

    static {
        ALL_PICKERS.put("LUMINANCE", new LuminancePicker());
        ALL_PICKERS.put("DEFAULT", new BruteForcePicker());
        ALL_PICKERS.put("BRUTEFORCE", new BruteForcePicker());
        ALL_PICKERS.put("BRUTE", new BruteForcePicker());
    }

    public static final Palette BNW = new Palette("BlackAndWhite", new LuminancePicker(), Color.WHITE, Color.BLACK);
    public static final Palette CGA1 = new Palette("CGA1", Color.BLACK, Color.decode("#AA00AA"), Color.decode("#00AAAA"), Color.decode("#AAAAAA"));
    public static final Palette CGA2 = new Palette("CGA2", Color.BLACK, Color.decode("#AA0000"), Color.decode("#00AA00"), Color.decode("#AA5500"));
    public static final Palette CGA3 = new Palette("CGA3", Color.BLACK, Color.decode("#AA0000"), Color.decode("#00AAAA"), Color.decode("#AAAAAA"));
    public static final Palette CGA16 = new Palette("CGA16",
            Color.BLACK, Color.decode("#555555"),
            Color.decode("#0000AA"), Color.decode("#5555FF"),
            Color.decode("#00AAAA"), Color.decode("#55FF55"),
            Color.decode("#AA0000"), Color.decode("#FF5555"),
            Color.decode("#AA00AA"), Color.decode("#FF55FF"),
            Color.decode("#AA5500"), Color.decode("#FFFF55"),
            Color.decode("#AAAAAA"), Color.WHITE);
    public static final Palette CGA = new Palette("CGA", CGA1);

    public static final Palette MONOCHROME = new Palette("Monochrome", BNW);

    private String name;
    private Color[] palette;
    private ColorPicker colorPicker;

    public Palette(String name, Collection<Color> colors) {
        this(name, colors.toArray(new Color[0]));
    }

    public Palette(String name, ColorPicker colorPicker, Collection<Color> colors) {
        this(name, colorPicker, colors.toArray(new Color[0]));
    }

    public static Palette get(String name) {
        return ALL.get(name);
    }

    Palette(String name, Palette p) {
        this(name, p.colorPicker, p.palette);
    }

    Palette(String name, ColorPicker colorPicker, Color... palette) {
        this.name = name;
        this.palette = palette;
        this.colorPicker = colorPicker;
        ALL.put(name, this);
    }

    Palette(String name, Color... palette) {
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
        return Optional.ofNullable(ALL_PICKERS.get(s)).orElseGet(BruteForcePicker::new);
    }

    public interface ColorPicker {
        Color closestColor(Color c, Color[] palette);
    }

    private static class LuminancePicker extends BruteForcePicker {

        @Override
        public double distance(final Color a, final Color b) {
            // Compare the difference of two RGB values, weigh by CCIR 601 luminosity:
            final double luma1 = (a.getRed() * 299 + a.getGreen() * 587 + a.getBlue() * 114) / 1000.0;
            final double luma2 = (b.getRed() * 299 + b.getGreen() * 587 + b.getBlue() * 114) / 1000.0;
            final double lumadiff = luma1 - luma2;
            final double diffR = (a.getRed() - b.getRed());
            final double diffG = (a.getGreen() - b.getGreen());
            final double diffB = (a.getBlue() - b.getBlue());
            return (diffR * diffR * 0.299 + diffG * diffG * 0.587 + diffB * diffB * 0.114) * 0.75
                    + lumadiff * lumadiff;
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

        public double distance(Color c1, Color c2) {
            int Rdiff = c1.getRed() - c2.getRed();
            int Gdiff = c1.getGreen() - c2.getBlue();
            int Bdiff = c1.getGreen() - c2.getBlue();
            return Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
        }
    }
}
