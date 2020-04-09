package org.sr3u.photoframe.client.filters.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Grayscale extends Palette {

    public Grayscale(String name) {
        super(name, new LuminancePicker(), createColors());
    }

    private static List<Color> createColors() {
        List<Color> colors = new ArrayList<>(256);
        for (int i = 0; i < 256; i++) {
            colors.add(new Color(i, i, i, 255));
        }
        return colors;
    }
}
