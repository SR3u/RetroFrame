package org.sr3u.photoframe.client.filters;


import org.sr3u.photoframe.misc.util.ImageUtil;
import sr3u.streamz.functionals.primitive.integer.IntFunctionex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DOT implements ImageFilter {

    private List<List<IntFunctionex<Integer>>> masks = new ArrayList<>();

    public DOT() {
        masks.add(new ArrayList<>());
        masks.add(new ArrayList<>());
        masks.get(0).add(i -> {
            Color c = new Color(i);
            return new Color(c.getRed() / 2, c.getBlue() / 2, c.getAlpha() / 2).getRGB();
        });
        masks.get(0).add(i -> 0xffff0000 & i);
        masks.get(1).add(i -> 0xff00ff00 & i);
        masks.get(1).add(i -> 0xff0000ff & i);
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }

    @Override
    public Image apply(Image image) throws Exception {
        BufferedImage out = ImageUtil.bufferedCopy(image);
        for (int y = 0; y < out.getHeight(); y++) {
            for (int x = 0; x < out.getWidth(); x++) {
                int rgb = out.getRGB(x, y);
                out.setRGB(x, y, function(x, y).apply(rgb));
            }
        }
        return out;
    }

    private IntFunctionex<Integer> function(int x, int y) {
        List<IntFunctionex<Integer>> row = masks.get(y % masks.size());
        return row.get(y % row.size());
    }

}
