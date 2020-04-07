package org.sr3u.photoframe.client;

import com.twelvemonkeys.image.ConvolveWithEdgeOp;
import org.sr3u.photoframe.client.filters.Identity;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.misc.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ImagePanel extends JComponent {
    private final ImageFilter imageFilter;
    private Image image;
    private Image originalImage;
    private Image blurryBackgroundImage;
    BufferedImageOp blur = createBlurOp();
    private ExecutorService executorService;

    int previousWidth = 0;
    int previousHeight = 0;


    public ImagePanel(Image image) {
        this();
        this.image = image;
    }

    public ImagePanel() {
        this(new Identity());
    }

    public ImagePanel(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        adjustSize(d.getWidth(), d.getHeight());
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        adjustSize(width, height);
    }

    private void adjustSize(double width, double height) {
        if (originalImage != null &&
                previousWidth != width &&
                previousHeight != height) {
            applyFiltersAsync(width, height);
            previousWidth = (int) width;
            previousHeight = (int) height;
        }

    }

    private Image blur(double width, double height, Image img) {
        Image scaledInstance = img.getScaledInstance(Math.min(256, (int) width), Math.min(256, (int) height), Image.SCALE_FAST);
        return blur.filter(ImageUtil.buffer(scaledInstance), null).getScaledInstance((int) (width + (width / 5)), (int) (height + height / 5), Image.SCALE_FAST);
    }

    private void applyFiltersAsync(double width, double height) {
        executorService.submit(() -> {
            if (originalImage == null) {
                return;
            }
            Image newImage = imageFilter.wrap().apply(ImageUtil.scaledImage(originalImage, width, height));
            Image newBackground = blur(width, height, newImage);
            newBackground = imageFilter.wrap().apply(newBackground);
            image = newImage;
            blurryBackgroundImage = newBackground;
            forceRedraw();
        });
    }

    private void forceRedraw() {
        invalidate();
        validate();
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        originalImage = image;
        adjustSize(getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blurryBackgroundImage != null) {
            int dw = getWidth() / 10;
            int dh = getHeight() / 10;
            g.drawImage(blurryBackgroundImage, -dw, -dh, getWidth() + 2 * dw, getHeight() + 2 * dh, this);
        }
        if (image != null) {
            Dimension size = ImageUtil.scaledSize(image, getWidth(), getHeight());
            int dw = Math.abs(avg(getWidth(), size.width));
            int dh = Math.abs(avg(getHeight(), size.height));
            g.drawImage(image, dw, dh, size.width, size.height, this);
        }
    }

    private BufferedImageOp createBlurOp() {
        int radius = 11;
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];
        Arrays.fill(data, weight);
        Kernel kernel = new Kernel(size, size, data);
        return new ConvolveWithEdgeOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
    }

    private int avg(int a, int b) {
        return (a - b) / 2;
    }
}
