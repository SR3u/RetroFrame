package org.sr3u.retroframe.filters.utils;

import java.awt.*;

public class DefinedPalettes {
    public static final PredefinedPalette BNW = new PredefinedPalette("BlackAndWhite", new LuminancePicker(),
            Color.WHITE, Color.BLACK).toPredefined();

    public static final PredefinedPalette CGA_Mode_4_0_Low = Palette.parse("CGA_Mode_4_0_High", "BRUTEFORCE #000000 " +
            "#00AA00 #AA0000 #AA5500").toPredefined();
    public static final PredefinedPalette CGA_Mode_4_0_High = Palette.parse("CGA_Mode_4_0_High", "BRUTEFORCE #000000 " +
            "#55FF55 #FF5555 #FF5555").toPredefined();
    public static final PredefinedPalette CGA_Mode_4_1_Low = Palette.parse("CGA_Mode_4_1_Low", "BRUTEFORCE #000000 " +
            "#00AAAA #AA00AA #AAAAAA").toPredefined();
    public static final PredefinedPalette CGA_Mode_4_1_High = Palette.parse("CGA_Mode_4_1_High", "BRUTEFORCE #000000 " +
            "#55FFFF #FF55FF #FFFFFF").toPredefined();
    public static final PredefinedPalette CGA_Mode_5_Low = Palette.parse("CGA_Mode_5_Low", "BRUTEFORCE #000000 " +
            "#00AAAA #AA0000 #AAAAAA").toPredefined();
    public static final PredefinedPalette CGA_Mode_5_High = Palette.parse("CGA_Mode_5_High", "BRUTEFORCE #000000 " +
            "#55FFFF #FF5555 #FFFFFF").toPredefined();
    public static final PredefinedPalette CGA16 = new PredefinedPalette("CGA16",
            Color.BLACK, Color.decode("#555555"),
            Color.decode("#0000AA"), Color.decode("#5555FF"),
            Color.decode("#00AAAA"), Color.decode("#55FF55"),
            Color.decode("#AA0000"), Color.decode("#FF5555"),
            Color.decode("#AA00AA"), Color.decode("#FF55FF"),
            Color.decode("#AA5500"), Color.decode("#FFFF55"),
            Color.decode("#AAAAAA"), Color.WHITE);
    public static final PredefinedPalette CGA = new PredefinedPalette("CGA", CGA_Mode_4_1_High);

    public static final PredefinedPalette C64 = new PredefinedPalette("C64", new BruteForcePicker(),
            Color.decode("#000000"), Color.decode("#626262"), Color.decode("#898989"), Color.decode("#adadad"),
            Color.decode("#ffffff"), Color.decode("#9f4e44"), Color.decode("#cb7e75"), Color.decode("#6d5412"),
            Color.decode("#a1683c"), Color.decode("#c9d487"), Color.decode("#9ae29b"), Color.decode("#5cab5e"),
            Color.decode("#6abfc6"), Color.decode("#887ecb"), Color.decode("#50459b"), Color.decode("#a057a3")
    ).toPredefined();

    public static final PredefinedPalette MONOCHROME = new PredefinedPalette("monochrome", BNW);

    // Apple Macintosh
    public static final PredefinedPalette MACINTOSH = new PredefinedPalette("MACINTOSH", BNW);
    public static final PredefinedPalette MACINTOSH16 = Palette.parse("Macintosh16", "bruteforce " +
            "#FFFFFF #FBF305 #FF6403 #DD0907 #F20884 #4700A5 #0000D3 #02ABEA " +
            "#1FB714 #006412 #562C05 #90713A #C0C0C0 #808080 #404040 #000000").toPredefined();
    public static final PredefinedPalette MACINTOSHII = new PredefinedPalette("MacintoshII",
            MACINTOSH16.toPredefined());
    public static final PredefinedPalette MACINTOSH2 = new PredefinedPalette("Macintosh2", MACINTOSH16.toPredefined());
    public static final PredefinedPalette MACINTOSH_SYSTEM_4_1 = new PredefinedPalette("Macintosh_System_4.1",
            MACINTOSH16);

    // ZX Spectrum
    public static final PredefinedPalette BRIGHT0 = Palette.parse("BRIGHT0", "bruteforce #000000 #0000D7 #D70000 " +
            "#D700D7 #00D700 #00D7D7 #D7D700 #D7D7D7").toPredefined();
    public static final PredefinedPalette BRIGHT1 = Palette.parse("BRIGHT1", "bruteforce #000000 #0000FF #FF0000 " +
            "#FF00FF #00FF00 #00FFFF #FFFF00 #FFFFFF").toPredefined();
    public static final PredefinedPalette ZX_BRIGHT0 = new PredefinedPalette("zxbright0", BRIGHT0.toPredefined());
    public static final PredefinedPalette ZX_BRIGHT1 = new PredefinedPalette("zxbright1", BRIGHT1.toPredefined());
    public static final PredefinedPalette ZX_0 = new PredefinedPalette("zx0", ZX_BRIGHT0.toPredefined());
    public static final PredefinedPalette ZX_1 = new PredefinedPalette("zx1", ZX_BRIGHT1.toPredefined());
    public static final PredefinedPalette ZX_FULL = Palette.parse("ZXFULL", "bruteforce " +
            "#000000 #0000D7 #D70000 #D700D7 #00D700 #00D7D7 #D7D700 #D7D7D7 " +
            "#0000FF #FF0000 #FF00FF #00FF00 #00FFFF #FFFF00 #FFFFFF").toPredefined();

    // Miscellaneous

    public static final PredefinedPalette XTERM = Palette.parse("XTERM", "bruteforce " +
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
            "#dadada #e4e4e4 #eeeeee").toPredefined();

    public static final PredefinedPalette RISCOS = Palette.parse("RISCOS", "bruteforce " +
            "#FFFFFF #DDDDDD #BBBBBB #999999 #777777 #555555 #333333 #000000 " +
            "#004499 #EEEE00 #00CC00 #DD0000 #EEEEBB #558800 #FFBB00 #00BBFF").toPredefined();

    public static final PredefinedPalette WINDOWS_16 = Palette.parse("Windows16", "bruteforce " +
            "#000000 #800000 #00800 #80800 #000080 #800080 #008080 #C0C0C0 " +
            "#808080 #FF0000 #00FF00 #FFFF00 #0000FF #FF00FF #00FFFF #FFFFFF").toPredefined();

    public static final PredefinedPalette WINDOWS_20 = Palette.parse("Windows20", "bruteforce " +
            "#000000 #800000 #008000 #808000 #000080 #800080 #008080 #C0C0C0 #C0DCC0 #A6CAF0 " +
            "#FFFBF0 #A0A0A4 #808080 #FF0000 #00FF00 #FFFF00 #0000FF #FF00FF #00FFFF #FFFFFF").toPredefined();

    public static final PredefinedPalette WINDOWS = new PredefinedPalette("Windows", WINDOWS_16).toPredefined();

    public static final Palette RGB222 = new RGB("RGB222", 2, 2, 2);
    public static final Palette RGB666 = new RGB("RGB666", 6, 6, 6);
    public static final Palette RGB676 = new RGB("RGB676", 6, 7, 6);
    public static final Palette RGB685 = new RGB("RGB685", 6, 8, 5);
    public static final Palette RGB884 = new RGB("RGB884", 8, 8, 4);

    public static final Grayscale GRAYSCALE = new Grayscale("Grayscale", 256);
    public static final MonochromeScale CYAN = new MonochromeScale("Cyan", Color.decode("#00FFFF"), 256);
    public static final MonochromeScale MAGENTA = new MonochromeScale("Magenta", Color.decode("#FF00FF"), 256);
    public static final MonochromeScale YELLOW = new MonochromeScale("Yellow", Color.decode("#FFFF00"), 256);
    public static final MonochromeScale RED = new MonochromeScale("Red", Color.decode("#FF0000"), 256);
    public static final MonochromeScale GREEN = new MonochromeScale("Green", Color.decode("#00FF00"), 256);
    public static final MonochromeScale BLUE = new MonochromeScale("Blue", Color.decode("#0000FF"), 256);
    public static final Palette CMYK = new MultiPalette("CMYK", new LuminancePicker(), CYAN, MAGENTA, YELLOW, BNW);

    public static final PredefinedPalette XORG = Palette.parse("Xorg", "bruteforce " +
            "#F0F8FF #FAEBD7 #00FFFF #7FFFD4 #F0FFFF #F5F5DC #FFE4C4 #000000 #FFEBCD #0000FF #8A2BE2 " +
            "#A52A2A #DEB887 #5F9EA0 #7FFF00 #D2691E #FF7F50 #6495ED #FFF8DC #DC143C #00FFFF #00008B " +
            "#008B8B #B8860B #A9A9A9 #006400 #BDB76B #8B008B #556B2F #FF8C00 #9932CC #8B0000 #E9967A " +
            "#8FBC8F #483D8B #2F4F4F #00CED1 #9400D3 #FF1493 #00BFFF #696969 #1E90FF #B22222 #FFFAF0 " +
            "#228B22 #FF00FF #DCDCDC #F8F8FF #FFD700 #DAA520 #BEBEBE #808080 #00FF00 #008000 #ADFF2F " +
            "#F0FFF0 #FF69B4 #CD5C5C #4B0082 #FFFFF0 #F0E68C #E6E6FA #FFF0F5 #7CFC00 #FFFACD #ADD8E6 " +
            "#F08080 #E0FFFF #FAFAD2 #D3D3D3 #90EE90 #FFB6C1 #FFA07A #20B2AA #87CEFA #778899 #B0C4DE " +
            "#FFFFE0 #00FF00 #32CD32 #FAF0E6 #FF00FF #B03060 #800000 #66CDAA #0000CD #BA55D3 #9370DB " +
            "#3CB371 #7B68EE #00FA9A #48D1CC #C71585 #191970 #F5FFFA #FFE4E1 #FFE4B5 #FFDEAD #000080 " +
            "#FDF5E6 #808000 #6B8E23 #FFA500 #FF4500 #DA70D6 #EEE8AA #98FB98 #AFEEEE #DB7093 #FFEFD5 " +
            "#FFDAB9 #CD853F #FFC0CB #DDA0DD #B0E0E6 #A020F0 #800080 #663399 #FF0000 #BC8F8F #4169E1 " +
            "#8B4513 #FA8072 #F4A460 #2E8B57 #FFF5EE #A0522D #C0C0C0 #87CEEB #6A5ACD #708090 #FFFAFA " +
            "#00FF7F #4682B4 #D2B48C #008080 #D8BFD8 #FF6347 #40E0D0 #EE82EE #F5DEB3 #FFFFFF #F5F5F5 " +
            "#FFFF00 #9ACD32").toPredefined();

    public static final PredefinedPalette X11_EXT_NOGRAY = Palette.parse("X11ext_noGray", "bruteforce " +
            "#FAEBD7 #FFEFDB #EEDFCC #CDC0B0 #8B8378 #7FFFD4 #76EEC6 #66CDAA #458B74 #F0FFFF #E0EEEE " +
            "#C1CDCD #838B8B #FFE4C4 #EED5B7 #CDB79E #8B7D6B #0000FF #0000EE #0000CD #00008B #A52A2A " +
            "#FF4040 #EE3B3B #CD3333 #8B2323 #DEB887 #FFD39B #EEC591 #CDAA7D #8B7355 #5F9EA0 #98F5FF " +
            "#8EE5EE #7AC5CD #53868B #7FFF00 #76EE00 #66CD00 #458B00 #D2691E #FF7F24 #EE7621 #CD661D " +
            "#8B4513 #FF7F50 #FF7256 #EE6A50 #CD5B45 #8B3E2F #FFF8DC #EEE8CD #CDC8B1 #8B8878 #00FFFF " +
            "#00EEEE #00CDCD #008B8B #B8860B #FFB90F #EEAD0E #CD950C #8B6508 #556B2F #CAFF70 #BCEE68 " +
            "#A2CD5A #6E8B3D #FF8C00 #FF7F00 #EE7600 #CD6600 #8B4500 #9932CC #BF3EFF #B23AEE #9A32CD " +
            "#68228B #8FBC8F #C1FFC1 #B4EEB4 #9BCD9B #698B69 #2F4F4F #97FFFF #8DEEEE #79CDCD #528B8B " +
            "#FF1493 #EE1289 #CD1076 #8B0A50 #00BFFF #00B2EE #009ACD #00688B #1E90FF #1C86EE #1874CD " +
            "#104E8B #B22222 #FF3030 #EE2C2C #CD2626 #8B1A1A #FFD700 #EEC900 #CDAD00 #8B7500 #DAA520 " +
            "#FFC125 #EEB422 #CD9B1D #8B6914 #00FF00 #00EE00 #00CD00 #008B00 #F0FFF0 #E0EEE0 #C1CDC1 " +
            "#838B83 #FF69B4 #FF6EB4 #EE6AA7 #CD6090 #8B3A62 #CD5C5C #FF6A6A #EE6363 #CD5555 #8B3A3A " +
            "#FFFFF0 #EEEEE0 #CDCDC1 #8B8B83 #F0E68C #FFF68F #EEE685 #CDC673 #8B864E #FFF0F5 #EEE0E5 " +
            "#CDC1C5 #8B8386 #FFFACD #EEE9BF #CDC9A5 #8B8970 #ADD8E6 #BFEFFF #B2DFEE #9AC0CD #68838B " +
            "#E0FFFF #D1EEEE #B4CDCD #7A8B8B #EEDD82 #FFEC8B #EEDC82 #CDBE70 #8B814C #FFB6C1 #FFAEB9 " +
            "#EEA2AD #CD8C95 #8B5F65 #FFA07A #EE9572 #CD8162 #8B5742 #87CEFA #B0E2FF #A4D3EE #8DB6CD " +
            "#607B8B #B0C4DE #CAE1FF #BCD2EE #A2B5CD #6E7B8B #FFFFE0 #EEEED1 #CDCDB4 #8B8B7A #FF00FF " +
            "#EE00EE #CD00CD #8B008B #B03060 #FF34B3 #EE30A7 #CD2990 #8B1C62 #BA55D3 #E066FF #D15FEE " +
            "#B452CD #7A378B #9370DB #AB82FF #9F79EE #8968CD #5D478B #FFE4E1 #EED5D2 #CDB7B5 #8B7D7B " +
            "#FFDEAD #EECFA1 #CDB38B #8B795E #6B8E23 #C0FF3E #B3EE3A #9ACD32 #698B22 #FFA500 #EE9A00 " +
            "#CD8500 #8B5A00 #FF4500 #EE4000 #CD3700 #8B2500 #DA70D6 #FF83FA #EE7AE9 #CD69C9 #8B4789 " +
            "#98FB98 #9AFF9A #90EE90 #7CCD7C #548B54 #AFEEEE #BBFFFF #AEEEEE #96CDCD #668B8B #DB7093 " +
            "#FF82AB #EE799F #CD6889 #8B475D #FFDAB9 #EECBAD #CDAF95 #8B7765 #FFC0CB #FFB5C5 #EEA9B8 " +
            "#CD919E #8B636C #DDA0DD #FFBBFF #EEAEEE #CD96CD #8B668B #A020F0 #9B30FF #912CEE #7D26CD " +
            "#551A8B #FF0000 #EE0000 #CD0000 #8B0000 #BC8F8F #FFC1C1 #EEB4B4 #CD9B9B #8B6969 #4169E1 " +
            "#4876FF #436EEE #3A5FCD #27408B #FA8072 #FF8C69 #EE8262 #CD7054 #8B4C39 #2E8B57 #54FF9F " +
            "#4EEE94 #43CD80 #2E8B57 #FFF5EE #EEE5DE #CDC5BF #8B8682 #A0522D #FF8247 #EE7942 #CD6839 " +
            "#8B4726 #87CEEB #87CEFF #7EC0EE #6CA6CD #4A708B #6A5ACD #836FFF #7A67EE #6959CD #473C8B " +
            "#708090 #C6E2FF #B9D3EE #9FB6CD #6C7B8B #FFFAFA #EEE9E9 #CDC9C9 #8B8989 #00FF7F #00EE76 " +
            "#00CD66 #008B45 #4682B4 #63B8FF #5CACEE #4F94CD #36648B #D2B48C #FFA54F #EE9A49 #CD853F " +
            "#8B5A2B #D8BFD8 #FFE1FF #EED2EE #CDB5CD #8B7B8B #FF6347 #EE5C42 #CD4F39 #8B3626 #40E0D0 " +
            "#00F5FF #00E5EE #00C5CD #00868B #D02090 #FF3E96 #EE3A8C #CD3278 #8B2252 #F5DEB3 #FFE7BA " +
            "#EED8AE #CDBA96 #8B7E66 #FFFF00 #EEEE00 #CDCD00 #8B8B00").toPredefined();

    public static final PredefinedPalette X11 = new PredefinedPalette("X11", XORG);
    public static final PredefinedPalette X_DOT_ORG = new PredefinedPalette("X.org", XORG);
    public static final PredefinedPalette X11_EXT = new MultiPalette("X11ext", X11, X11_EXT_NOGRAY).toPredefined();
    public static final PredefinedPalette X11_FULL = new PredefinedPalette("X11full", X11_EXT);

    public static final PredefinedPalette CRAYOLA = Palette.parse("Crayola", "luminance " +
            "#ED0A3F #C32148 #FD0E35 #C62D42 #CC474B #CC3336 #E12C2C #D92121 #B94E48 #FF5349 #FE4C40 " +
            "#FE6F5E #B33B24 #CC553D #E6735C #FF9980 #E58E73 #FF7034 #FF681F #FF8833 #FFB97B #ECAC76 " +
            "#E77200 #FFAE42 #F2BA49 #FBE7B2 #F2C649 #F8D568 #FCD667 #FED85D #FBE870 #F1E788 #FFEB00 " +
            "#B5B35C #ECEBBD #FAFA37 #FFFF99 #FFFF9F #D9E650 #ACBF60 #AFE313 #BEE64B #C5E17A #5E8C31 " +
            "#7BA05B #9DE093 #63B76C #4D8C57 #3AA655 #6CA67C #5FA777 #93DFB8 #33CC99 #1AB385 #29AB87 " +
            "#00CC99 #00755E #8DD9CC #01786F #30BFBF #00CCCC #008080 #8FD8D8 #95E0E8 #6CDAE7 #2D383A " +
            "#76D7EA #7ED4E6 #0095B7 #009DC4 #02A4D3 #47ABCC #2EB4E6 #339ACC #93CCEA #2887C8 #003366 " +
            "#0066CC #1560BD #0066FF #A9B2C3 #C3CDE6 #4570E6 #3C69E7 #7A89B8 #4F69C6 #8D90A1 #8C90C8 " +
            "#7070CC #9999CC #ACACE6 #766EC8 #6456B7 #3F26BF #8B72BE #652DC1 #6B3FA0 #8359A3 #8F47B3 " +
            "#C9A0DC #BF8FCC #803790 #733380 #D6AEDD #C154C1 #FC74FD #732E6C #E667CE #E29CD2 #8E3179 " +
            "#D96CBE #EBB0D7 #C8509B #BB3385 #D982B5 #A63A79 #A50B5E #614051 #F653A6 #DA3287 #FF3399 " +
            "#FBAED2 #FFB7D5 #FFA6C9 #F7468A #E30B5C #FDD7E4 #E62E6B #DB5079 #FC80A5 #F091A9 #FF91A4 " +
            "#A55353 #CA3435 #FEBAAD #F7A38E #E97451 #AF593E #9E5B40 #87421F #926F5B #DEA681 #D27D46 " +
            "#664228 #FA9D5A #EDC9AF #FFCBA4 #805533 #FDD5B1 #EED9C4 #665233 #837050 #E6BC5C #D9D6CF " +
            "#92926E #E6BE8A #C9C0BB #DA8A67 #C88A65 #000000 #736A62 #8B8680 #C8C8CD #FFFFFF").toPredefined();

    public static final PredefinedPalette GAMEBOY = Palette.parse("GameBoy", "luminance #0F380F #306230 #8BAC0F " +
            "#9BBC0F").toPredefined();

    public static final PredefinedPalette WEBSAFE = Palette.parse("WebSafe", "luminance " +
            "#000000 #000033 #000066 #000099 #0000CC #0000FF #003300 #003333 #003366 #003399 #0033CC #0033FF #006600 " +
            "#006633 #006666 #006699 #0066CC #0066FF #009900 #009933 #009966 #009999 #0099CC #0099FF #00CC00 #00CC33 " +
            "#00CC66 #00CC99 #00CCCC #00CCFF #00FF00 #00FF33 #00FF66 #00FF99 #00FFCC #00FFFF #330000 #330033 #330066 " +
            "#330099 #3300CC #3300FF #333300 #333333 #333366 #333399 #3333CC #3333FF #336600 #336633 #336666 #336699 " +
            "#3366CC #3366FF #339900 #339933 #339966 #339999 #3399CC #3399FF #33CC00 #33CC33 #33CC66 #33CC99 #33CCCC " +
            "#33CCFF #33FF00 #33FF33 #33FF66 #33FF99 #33FFCC #33FFFF #660000 #660033 #660066 #660099 #6600CC #6600FF " +
            "#663300 #663333 #663366 #663399 #6633CC #6633FF #666600 #666633 #666666 #666699 #6666CC #6666FF #669900 " +
            "#669933 #669966 #669999 #6699CC #6699FF #66CC00 #66CC33 #66CC66 #66CC99 #66CCCC #66CCFF #66FF00 #66FF33 " +
            "#66FF66 #66FF99 #66FFCC #66FFFF #990000 #990033 #990066 #990099 #9900CC #9900FF #993300 #993333 #993366 " +
            "#993399 #9933CC #9933FF #996600 #996633 #996666 #996699 #9966CC #9966FF #999900 #999933 #999966 #999999 " +
            "#9999CC #9999FF #99CC00 #99CC33 #99CC66 #99CC99 #99CCCC #99CCFF #99FF00 #99FF33 #99FF66 #99FF99 #99FFCC " +
            "#99FFFF #CC0000 #CC0033 #CC0066 #CC0099 #CC00CC #CC00FF #CC3300 #CC3333 #CC3366 #CC3399 #CC33CC #CC33FF " +
            "#CC6600 #CC6633 #CC6666 #CC6699 #CC66CC #CC66FF #CC9900 #CC9933 #CC9966 #CC9999 #CC99CC #CC99FF #CCCC00 " +
            "#CCCC33 #CCCC66 #CCCC99 #CCCCCC #CCCCFF #CCFF00 #CCFF33 #CCFF66 #CCFF99 #CCFFCC #CCFFFF #FF0000 #FF0033 " +
            "#FF0066 #FF0099 #FF00CC #FF00FF #FF3300 #FF3333 #FF3366 #FF3399 #FF33CC #FF33FF #FF6600 #FF6633 #FF6666 " +
            "#FF6699 #FF66CC #FF66FF #FF9900 #FF9933 #FF9966 #FF9999 #FF99CC #FF99FF #FFCC00 #FFCC33 #FFCC66 #FFCC99 " +
            "#FFCCCC #FFCCFF #FFFF00 #FFFF33 #FFFF66 #FFFF99 #FFFFCC #FFFFFF").toPredefined();

    public static final Technicolor TECHNICOLOR = new Technicolor("Technicolor");

}
