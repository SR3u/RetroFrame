package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;

public class DefinedPalettes {
    public static final Palette BNW = new Palette("BlackAndWhite", new Palette.LuminancePicker(), Color.WHITE, Color.BLACK);

    public static final Palette CGA_Mode_4_0_Low = Palette.parse("CGA_Mode_4_0_High", "BRUTEFORCE #000000 #00AA00 #AA0000 #AA5500");
    public static final Palette CGA_Mode_4_0_High = Palette.parse("CGA_Mode_4_0_High", "BRUTEFORCE #000000 #55FF55 #FF5555 #FF5555");
    public static final Palette CGA_Mode_4_1_Low = Palette.parse("CGA_Mode_4_1_Low", "BRUTEFORCE #000000 #00AAAA #AA00AA #AAAAAA");
    public static final Palette CGA_Mode_4_1_High = Palette.parse("CGA_Mode_4_1_High", "BRUTEFORCE #000000 #55FFFF #FF55FF #FFFFFF");
    public static final Palette CGA_Mode_5_Low = Palette.parse("CGA_Mode_5_Low", "BRUTEFORCE #000000 #00AAAA #AA0000 #AAAAAA");
    public static final Palette CGA_Mode_5_High = Palette.parse("CGA_Mode_5_High", "BRUTEFORCE #000000 #55FFFF #FF5555 #FFFFFF");
    public static final Palette CGA16 = new Palette("CGA16",
            Color.BLACK, Color.decode("#555555"),
            Color.decode("#0000AA"), Color.decode("#5555FF"),
            Color.decode("#00AAAA"), Color.decode("#55FF55"),
            Color.decode("#AA0000"), Color.decode("#FF5555"),
            Color.decode("#AA00AA"), Color.decode("#FF55FF"),
            Color.decode("#AA5500"), Color.decode("#FFFF55"),
            Color.decode("#AAAAAA"), Color.WHITE);
    public static final Palette CGA = new Palette("CGA", CGA_Mode_4_1_High);

    public static final Palette C64 = new Palette("C64", new Palette.BruteForcePicker(),
            Color.decode("#000000"), Color.decode("#626262"), Color.decode("#898989"), Color.decode("#adadad"),
            Color.decode("#ffffff"), Color.decode("#9f4e44"), Color.decode("#cb7e75"), Color.decode("#6d5412"),
            Color.decode("#a1683c"), Color.decode("#c9d487"), Color.decode("#9ae29b"), Color.decode("#5cab5e"),
            Color.decode("#6abfc6"), Color.decode("#887ecb"), Color.decode("#50459b"), Color.decode("#a057a3")
    );

    public static final Palette MONOCHROME = new Palette("monochrome", BNW);

    // Apple Macintosh
    public static final Palette MACINTOSH = new Palette("MACINTOSH", BNW);
    public static final Palette MACINTOSH16 = Palette.parse("Macintosh16", "bruteforce " +
            "#FFFFFF #FBF305 #FF6403 #DD0907 #F20884 #4700A5 #0000D3 #02ABEA " +
            "#1FB714 #006412 #562C05 #90713A #C0C0C0 #808080 #404040 #000000");
    public static final Palette MACINTOSHII = new Palette("MacintoshII", MACINTOSH16);
    public static final Palette MACINTOSH2 = new Palette("Macintosh2", MACINTOSH16);
    public static final Palette MACINTOSH_SYSTEM_4_1 = new Palette("Macintosh_System_4.1", MACINTOSH16);

    // ZX Spectrum
    public static final Palette BRIGHT0 = Palette.parse("BRIGHT0", "bruteforce #000000 #0000D7 #D70000 #D700D7 #00D700 #00D7D7 #D7D700 #D7D7D7");
    public static final Palette BRIGHT1 = Palette.parse("BRIGHT1", "bruteforce #000000 #0000FF #FF0000 #FF00FF #00FF00 #00FFFF #FFFF00 #FFFFFF");
    public static final Palette ZX_BRIGHT0 = new Palette("zxbright0", BRIGHT0);
    public static final Palette ZX_BRIGHT1 = new Palette("zxbright1", BRIGHT1);
    public static final Palette ZX_0 = new Palette("zx0", ZX_BRIGHT0);
    public static final Palette ZX_1 = new Palette("zx1", ZX_BRIGHT1);
    public static final Palette ZX_FULL = Palette.parse("ZXFULL", "bruteforce " +
            "#000000 #0000D7 #D70000 #D700D7 #00D700 #00D7D7 #D7D700 #D7D7D7 " +
            "#0000FF #FF0000 #FF00FF #00FF00 #00FFFF #FFFF00 #FFFFFF");

    // Miscellaneous

    public static final Palette XTERM = Palette.parse("XTERM", "bruteforce " +
            "#000000 #800000 #008000 #808000 #000080 #800080 #008080 #c0c0c0 #808080 #ff0000 #00ff00 " +
            "#ffff00 #0000ff #ff00ff #00ffff #ffffff #000000 #00005f #000087 #0000af #0000d7 #0000ff " +
            "#005f00 #005f5f #005f87 #005faf #005fd7 #005fff #008700 #00875f #008787 #0087af #0087d7 " +
            "#0087ff #00af00 #00af5f #00af87 #00afaf #00afd7 #00afff #00d700 #00d75f #00d787 #00d7af " +
            "#00d7d7 #00d7ff #00ff00 #00ff5f #00ff87 #00ffaf #00ffd7 #00ffff #5f0000 #5f005f #5f0087 " +
            "#5f00af #5f00d7 #5f00ff #5f5f00 #5f5f5f #5f5f87 #5f5faf #5f5fd7 #5f5fff #5f8700 #5f875f " +
            "#5f8787 #5f87af #5f87d7 #5f87ff #5faf00 #5faf5f #5faf87 #5fafaf #5fafd7 #5fafff #5fd700 " +
            "#5fd75f #5fd787 #5fd7af #5fd7d7 #5fd7ff #5fff00 #5fff5f #5fff87 #5fffaf #5fffd7 #5fffff " +
            "#870000 #87005f #870087 #8700af #8700d7 #8700ff #875f00 #875f5f #875f87 #875faf #875fd7 " +
            "#875fff #878700 #87875f #878787 #8787af #8787d7 #8787ff #87af00 #87af5f #87af87 #87afaf " +
            "#87afd7 #87afff #87d700 #87d75f #87d787 #87d7af #87d7d7 #87d7ff #87ff00 #87ff5f #87ff87 " +
            "#87ffaf #87ffd7 #87ffff #af0000 #af005f #af0087 #af00af #af00d7 #af00ff #af5f00 #af5f5f " +
            "#af5f87 #af5faf #af5fd7 #af5fff #af8700 #af875f #af8787 #af87af #af87d7 #af87ff #afaf00 " +
            "#afaf5f #afaf87 #afafaf #afafd7 #afafff #afd700 #afd75f #afd787 #afd7af #afd7d7 #afd7ff " +
            "#afff00 #afff5f #afff87 #afffaf #afffd7 #afffff #d70000 #d7005f #d70087 #d700af #d700d7 " +
            "#d700ff #d75f00 #d75f5f #d75f87 #d75faf #d75fd7 #d75fff #d78700 #d7875f #d78787 #d787af " +
            "#d787d7 #d787ff #d7af00 #d7af5f #d7af87 #d7afaf #d7afd7 #d7afff #d7d700 #d7d75f #d7d787 " +
            "#d7d7af #d7d7d7 #d7d7ff #d7ff00 #d7ff5f #d7ff87 #d7ffaf #d7ffd7 #d7ffff #ff0000 #ff005f " +
            "#ff0087 #ff00af #ff00d7 #ff00ff #ff5f00 #ff5f5f #ff5f87 #ff5faf #ff5fd7 #ff5fff #ff8700 " +
            "#ff875f #ff8787 #ff87af #ff87d7 #ff87ff #ffaf00 #ffaf5f #ffaf87 #ffafaf #ffafd7 #ffafff " +
            "#ffd700 #ffd75f #ffd787 #ffd7af #ffd7d7 #ffd7ff #ffff00 #ffff5f #ffff87 #ffffaf #ffffd7 " +
            "#ffffff #080808 #121212 #1c1c1c #262626 #303030 #3a3a3a #444444 #4e4e4e #585858 #626262 " +
            "#6c6c6c #767676 #808080 #8a8a8a #949494 #9e9e9e #a8a8a8 #b2b2b2 #bcbcbc #c6c6c6 #d0d0d0 " +
            "#dadada #e4e4e4 #eeeeee");

    public static final Palette RISCOS = Palette.parse("RISCOS", "bruteforce " +
            "#FFFFFF #DDDDDD #BBBBBB #999999 #777777 #555555 #333333 #000000 " +
            "#004499 #EEEE00 #00CC00 #DD0000 #EEEEBB #558800 #FFBB00 #00BBFF");

    public static final Palette WINDOWS_16 = Palette.parse("Windows16", "bruteforce " +
            "#000000 #800000 #00800 #80800 #000080 #800080 #008080 #C0C0C0 " +
            "#808080 #FF0000 #00FF00 #FFFF00 #0000FF #FF00FF #00FFFF #FFFFFF");

    public static final Palette WINDOWS_20 = Palette.parse("Windows20", "bruteforce " +
            "#000000 #800000 #008000 #808000 #000080 #800080 #008080 #C0C0C0 #C0DCC0 #A6CAF0 " +
            "#FFFBF0 #A0A0A4 #808080 #FF0000 #00FF00 #FFFF00 #0000FF #FF00FF #00FFFF #FFFFFF");

    public static final Palette WINDOWS = new Palette("Windows", WINDOWS_16);

    public static final Palette RGB666 = new RGB("RGB666", 6, 6, 6);
    public static final Palette RGB676 = new RGB("RGB676", 6, 7, 6);
    public static final Palette RGB685 = new RGB("RGB685", 6, 8, 5);
    public static final Palette RGB884 = new RGB("RGB884", 8, 8, 4);

}
