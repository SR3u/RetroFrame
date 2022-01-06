package org.sr3u.retroframe.filters;

import lombok.Getter;
import org.sr3u.retroframe.filters.utils.ArgParser;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class Grayscale implements FastImageFilter {
    private double redC = 0.299;
    private double greenC = 0.587;
    private double blueC = 0.114;
    private double alphaC = 1.0;

    @Override
    public Object createContext(BufferedImage image) {
        return null;
    }

    @Override
    public void apply(BufferedImage image, Object contextObject, int x, int y) throws Exception {
        Color c = new Color(image.getRGB(x, y));
        int red = (int) (c.getRed() * redC);
        int green = (int) (c.getGreen() * greenC);
        int blue = (int) (c.getBlue() * blueC);
        int a = (int) (c.getAlpha() * alphaC);
        Color newColor = new Color(red + green + blue,
                red + green + blue,
                red + green + blue,
                a);
        image.setRGB(x, y, newColor.getRGB());
    }

    @Override
    public ImageFilter init(java.util.List<String> parameters) {
        ArgParser parser = new ArgParser(parameters);
        redC = parser.doubleAt(0).orElse(redC);
        greenC = parser.doubleAt(1).orElse(greenC);
        blueC = parser.doubleAt(2).orElse(blueC);
        alphaC = parser.doubleAt(3).orElse(alphaC);
        return this;
    }

    @Override
    public void reset() {
    }
}
