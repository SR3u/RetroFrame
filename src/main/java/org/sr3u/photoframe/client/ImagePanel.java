package org.sr3u.photoframe.client;

import com.twelvemonkeys.image.ConvolveWithEdgeOp;
import org.sr3u.photoframe.server.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

class ImagePanel extends JComponent {
    private Image image;
    private Image originalImage;
    private Image blurryBackgroundImage;
    BufferedImageOp blur = createBlurOp();

    public ImagePanel(Image image) {
        this.image = image;
    }

    public ImagePanel() {
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
        if (originalImage != null) {
            image = ImageUtil.scaledImage(originalImage, width, height);
            Image scaledInstance = image.getScaledInstance(Math.min(256, (int) width), Math.min(256, (int) height), Image.SCALE_FAST);
            blurryBackgroundImage = blur.filter(ImageUtil.buffer(scaledInstance), null).getScaledInstance((int) (width + (width / 5)), (int) (height + height / 5), Image.SCALE_FAST);
        }
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
            g.drawImage(blurryBackgroundImage, -blurryBackgroundImage.getWidth(null) / 10, -blurryBackgroundImage.getHeight(null) / 10, this);
        }
        if (image != null) {
            int imgWidth = image.getWidth(null);
            int imgHeight = image.getHeight(null);
            g.drawImage(image, avg(getWidth(), imgWidth), avg(getHeight(), imgHeight), this);
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
