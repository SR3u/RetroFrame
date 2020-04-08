package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;

public class DefinedPalettes {
    public static final Palette BNW = new Palette("BlackAndWhite", new Palette.LuminancePicker(), Color.WHITE, Color.BLACK);
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

    public static final Palette C64 = new Palette("C64", new Palette.BruteForcePicker(),
            Color.decode("#000000"), Color.decode("#626262"), Color.decode("#898989"), Color.decode("#adadad"),
            Color.decode("#ffffff"), Color.decode("#9f4e44"), Color.decode("#cb7e75"), Color.decode("#6d5412"),
            Color.decode("#a1683c"), Color.decode("#c9d487"), Color.decode("#9ae29b"), Color.decode("#5cab5e"),
            Color.decode("#6abfc6"), Color.decode("#887ecb"), Color.decode("#50459b"), Color.decode("#a057a3")
    );

    public static final Palette MONOCHROME = new Palette("monochrome", BNW);
}
