package org.sr3u.photoframe.client.filters;

import lombok.Getter;
import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter
public class Grayscale implements ImageFilter {
    private double redC = 0.299;
    private double greenC = 0.587;
    private double blueC = 0.114;

    @Override
    public Image apply(Image img) throws Exception {
        BufferedImage image = ImageUtil.bufferedCopy(img);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * redC);
                int green = (int) (c.getGreen() * greenC);
                int blue = (int) (c.getBlue() * blueC);
                int a = c.getAlpha();
                Color newColor = new Color(red + green + blue,
                        red + green + blue,
                        red + green + blue,
                        a);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
        return image;
    }

    @Override
    public ImageFilter init(java.util.List<String> parameters) {
        if (parameters.size() > 0) {
            redC = Double.parseDouble(parameters.get(2));
        }
        if (parameters.size() > 1) {
            greenC = Double.parseDouble(parameters.get(2));
        }
        if (parameters.size() > 2) {
            blueC = Double.parseDouble(parameters.get(2));
        }
        return this;
    }
}
