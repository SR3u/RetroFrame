package org.sr3u.retroframe.filters.zxspectrum;

import lombok.Data;
import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.utils.*;
import org.sr3u.retroframe.filters.utils.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Spectrum implements ImageFilter {

    protected static final Palette bright0 = new PredefinedPalette(null, new LuminancePicker(), DefinedPalettes.ZX_0);
    protected static final Palette bright1 = new PredefinedPalette(null, new LuminancePicker(), DefinedPalettes.ZX_1);
    protected int attributesW = 8;
    protected int attributesH = -1;

    @Override
    public Image apply(Image img) throws Exception {
        BufferedImage image = ImageUtil.buffer(img);
        Context context = new Context(image, attributesW, attributesH);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                Attributes attributes = context.getAttributes(x, y);
                Palette inkAndPaper = attributes.getInkAndPaper();
                Color newPixel = inkAndPaper.closestColor(pixel);
                image.setRGB(x, y, newPixel.getRGB());
            }
        }
        return image;
    }

    @Override
    public ImageFilter init(List<String> parameters) {
        ArgParser params = new ArgParser(parameters);
        attributesW = Math.abs(params.intAt(0).orElse(attributesW));
        attributesH = params.intAt(0).orElse(attributesH);
        return this;
    }

    private static class Context {
        final int attributeBlockWidth;
        final int attributeBlockHeight;

        final Attributes[][] attributes;
        private final int attributesSizeW;
        private final int attributesSizeH;

        public Context(BufferedImage image, int attributesW, int attributesH) {

            if (attributesH <= 0) {
                attributesH = image.getHeight() / attributesW;
                attributesW = image.getWidth() / attributesW;
            }

            attributeBlockWidth = Math.max((image.getWidth() / attributesW), 1);
            attributeBlockHeight = Math.max((image.getHeight() / attributesH), 1);
            this.attributesSizeW = Math.min(attributesW, image.getWidth());
            this.attributesSizeH = Math.min(attributesH, image.getHeight());

            attributes = new Attributes[attributesSizeH][attributesSizeW];
            for (int i = 0; i < attributesSizeH; i++) {
                for (int j = 0; j < attributesSizeW; j++) {
                    attributes[i][j] = new Attributes();
                    int charX = j * attributeBlockWidth;
                    int chary = i * attributeBlockHeight;
                    int[] rgbs = image.getRGB(charX, chary, attributeBlockWidth, attributeBlockHeight, null, 0, image.getWidth());
                    Map<Integer, Integer> colorFrequencies = Arrays.stream(rgbs).boxed().collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum));
                    Color mostFrequentColor = new Color(colorFrequencies.keySet().stream().max(Comparator.comparing(colorFrequencies::get)).orElse(0));
                    Color brightestColor = Arrays.stream(rgbs).distinct().mapToObj(Color::new)
                            .min(Comparator.comparing(c -> distance(c, mostFrequentColor))).orElse(Color.WHITE);
                    Color darkestColor = Arrays.stream(rgbs).distinct().mapToObj(Color::new)
                            .max(Comparator.comparing(c -> distance(c, mostFrequentColor))).orElse(Color.BLACK);
                    brightestColor = pickNewColor(attributes[i][j], brightestColor);
                    attributes[i][j].setInk(brightestColor);
                    if (attributes[i][j].bright) {
                        darkestColor = bright1.closestColor(darkestColor);
                    } else {
                        darkestColor = bright0.closestColor(darkestColor);
                    }
                    attributes[i][j].setPaper(darkestColor);
                    attributes[i][j].setInkAndPaper(new PredefinedPalette(null, new LuminancePicker(), darkestColor, brightestColor));
                }
            }
        }

        private double distance(Color c, Color mostFrequentColor) {
            return BruteForcePicker.squareDistance(c, mostFrequentColor) + LuminancePicker.luminanceDistance(c, Color.WHITE);
        }

        private Color pickNewColor(Attributes attributes, Color pixel) {
            Color b0 = bright0.closestColor(pixel);
            Color b1 = bright1.closestColor(pixel);
            if (BruteForcePicker.squareDistance(pixel, b0) > BruteForcePicker.squareDistance(pixel, b1)) {
                attributes.bright = false;
                return b0;
            } else {
                attributes.bright = true;
                return b1;
            }
        }

        public Attributes getAttributes(int x, int y) {
            return attributes[(y / attributeBlockHeight) % attributesSizeH][(x / attributeBlockWidth) % attributesSizeW];
        }
    }

    @Data
    public static class Attributes {
        private boolean flash = false; // bit7, unused
        private Boolean bright = null; // bit6, choose between BRIGHT0 and BRIGHT1 palettes
        private Color paper; // bits 5..3, paper (background) color
        private Color ink; // bits 2..0, ink (foreground) color
        private Palette inkAndPaper;
    }

    @Override
    public void reset() {
        bright0.reset();
        bright1.reset();
    }
}
