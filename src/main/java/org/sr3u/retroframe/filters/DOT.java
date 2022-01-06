package org.sr3u.retroframe.filters;


import sr3u.streamz.functionals.primitive.integer.IntFunctionex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DOT implements FastImageFilter {

    private List<List<IntFunctionex<Integer>>> masks = new ArrayList<>();

    public DOT() {
        masks.add(new ArrayList<>());
        masks.add(new ArrayList<>());
        masks.get(0).add(i -> {
            Color c = new Color(i);
            return new Color(c.getRed() / 2, c.getGreen(), c.getBlue() / 2, c.getAlpha()).getRGB();
        });
        masks.get(0).add(i -> 0xffff0000 & i);
        masks.get(1).add(i -> 0xff00ff00 & i);
        masks.get(1).add(i -> 0xff0000ff & i);
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    private IntFunctionex<Integer> function(int x, int y) {
        List<IntFunctionex<Integer>> row = masks.get(y % masks.size());
        return row.get(x % row.size());
    }

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        int rgb = image.getRGB(x, y);
        image.setRGB(x, y, function(x, y).apply(rgb));
    }

    @Override
    public void reset() {
    }
}
