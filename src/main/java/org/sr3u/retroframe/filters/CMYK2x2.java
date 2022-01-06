package org.sr3u.retroframe.filters;

import org.sr3u.retroframe.filters.utils.DefinedPalettes;
import org.sr3u.retroframe.filters.utils.Palette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class CMYK2x2 implements FastImageFilter {

    private Palette[][] palettes = new Palette[][]{
            new Palette[]{DefinedPalettes.GRAYSCALE, DefinedPalettes.CYAN},
            new Palette[]{DefinedPalettes.MAGENTA, DefinedPalettes.YELLOW},
    };

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        Color pixel = new Color(image.getRGB(x, y));
        Color newPixel = palettes[y % 2][x % 2].closestColor(pixel);
        image.setRGB(x, y, newPixel.getRGB());
    }

    @Override
    public void reset() {
        for (int i = 0; i < palettes.length; i++) {
            for (int j = 0; j < palettes[i].length; j++) {
                palettes[i][j].reset();
            }
        }
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        return this;
    }
}
