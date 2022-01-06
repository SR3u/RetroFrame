package org.sr3u.retroframe.filters.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RGB extends Palette {
    private final int levelsR;
    private final int levelsG;
    private final int levelsB;
    private final int levelsA;

    public RGB(String name, int levelsR, int levelsG, int levelsB) {
        this(name, levelsR, levelsG, levelsB, 255);
    }

    public RGB(String name, int levelsR, int levelsG, int levelsB, int levelsA) {
        super(name);
        this.levelsR = levelsR;
        this.levelsG = levelsG;
        this.levelsB = levelsB;
        this.levelsA = levelsA;
    }

    @Override
    protected PredefinedPalette toPredefined() {
        List<Color> colors = new ArrayList<>(levelsR * levelsG * levelsB * levelsA);
        for (int r = 0; r < levelsR; r++) {
            for (int g = 0; g < levelsG; g++) {
                for (int b = 0; b < levelsB; b++) {
                    for (int a = 0; a < levelsA; a++) {
                        colors.add(new Color(clamp(r * levelsR, levelsR),
                                clamp(g * levelsG, levelsG),
                                clamp(b * levelsB, levelsB),
                                clamp(a * levelsA, levelsA)));
                    }
                }
            }
        }
        return new PredefinedPalette(null, new LuminancePicker(), colors);
    }

    @Override
    public Color closestColor(Color c) {
        int r = clamp(c.getRed(), levelsR);
        int g = clamp(c.getGreen(), levelsG);
        int b = clamp(c.getBlue(), levelsB);
        int a = clamp(c.getAlpha(), levelsA);
        return new Color(r, g, b, a);
    }

    private int clamp(int c, int levels) {
        int clamp = 255 / levels;
        return ((c / clamp)) * clamp;
    }
}
