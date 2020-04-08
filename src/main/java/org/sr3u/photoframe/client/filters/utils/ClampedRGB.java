package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.Collections;

public class ClampedRGB extends Palette {
    private final int levelsR;
    private final int levelsG;
    private final int levelsB;
    private final int levelsA;

    public ClampedRGB(String name, int levelsR, int levelsG, int levelsB) {
        this(name, levelsR, levelsG, levelsB, 255);
    }

    public ClampedRGB(String name, int levelsR, int levelsG, int levelsB, int levelsA) {
        super(name, Collections.emptyList());
        this.levelsR = levelsR;
        this.levelsG = levelsG;
        this.levelsB = levelsB;
        this.levelsA = levelsA;
    }

    @Override
    public Color closestColor(Color c) {
        int r = clamp(c.getRed(), levelsR);
        int g = clamp(c.getGreen(), levelsG);
        int b = clamp(c.getBlue(), levelsB);
        int a = clamp(c.getAlpha(), levelsA);
        return new Color(r, g, b, a);
    }

    private int clamp(int c, float levels) {
        return (int) ((c / levels) * 255);
    }
}
