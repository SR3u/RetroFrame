package org.sr3u.retroframe.filters.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPalette extends Palette {
    private final List<Palette> palettes;
    private final ColorPicker colorPicker;

    public MultiPalette(Palette... palettes) {
        this(null, palettes);
    }

    public MultiPalette() {
        this((String) null);
    }

    public MultiPalette(String name) {
        this(name, new LuminancePicker());
    }

    public MultiPalette(String name, ColorPicker colorPicker) {
        this(name, colorPicker, (Palette[]) null);
    }

    public MultiPalette(String name, Palette... palettes) {
        this(name, new LuminancePicker(), palettes);
    }

    public MultiPalette(String name, ColorPicker colorPicker, Palette... palettes) {
        super(name);
        if (palettes == null) {
            this.palettes = new ArrayList<>();
        } else {
            this.palettes = Arrays.asList(palettes);
        }
        this.colorPicker = colorPicker;
    }

    @Override
    public Palette append(Palette... palettes) {
        this.palettes.addAll(Arrays.asList(palettes));
        return this;
    }

    @Override
    protected PredefinedPalette toPredefined() {
        List<Color> colors = new ArrayList<>();
        for (Palette p : palettes) {
            colors.addAll(Arrays.asList(p.toPredefined().getColors()));
        }
        return new PredefinedPalette(null, colorPicker, colors);
    }

    @Override
    public Color closestColor(Color c) {
        Color best = colorPicker.cachedColor(c.getRGB());
        if (best == null) {
            double bestD = Double.MAX_VALUE;
            for (Palette p : palettes) {
                Color n = p.closestColor(c);
                double distance = colorPicker.distance(c, n);
                if (distance < bestD) {
                    best = n;
                    bestD = distance;
                }
            }
        }
        return best;
    }

    @Override
    public void reset() {
        super.reset();
        colorPicker.reset();
        palettes.forEach(Palette::reset);
    }
}
