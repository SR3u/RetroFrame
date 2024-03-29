package org.sr3u.retroframe.client.ui.main;

import com.twelvemonkeys.image.ConvolveWithEdgeOp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.retroframe.filters.Identity;
import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.misc.util.DroppingExecutor;
import org.sr3u.retroframe.filters.utils.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class ImagePanel extends JComponent {

    private static final Logger log = LogManager.getLogger(ImagePanel.class);

    private ImageFilter imageFilter;
    private Image image;
    private Image originalImage;
    private Image blurryBackgroundImage;
    BufferedImageOp blur = createBlurOp();
    private final Executor executor = new DroppingExecutor(1, 2);

    int previousWidth = 0;
    int previousHeight = 0;
    private boolean forceAdjustSize = false;


    public ImagePanel(Image image) {
        this();
        this.image = image;
    }

    public ImagePanel() {
        this(new Identity());
    }

    public ImagePanel(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        adjustSize(d.getWidth(), d.getHeight(), imageFilter);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        adjustSize(width, height);
    }

    private CompletableFuture<Void> adjustSize(double width, double height) {
        return adjustSize(width, height, null);
    }

    private CompletableFuture<Void> adjustSize(double width, double height, ImageFilter imageFilter) {
        boolean sizeChanged = (previousWidth != width &&
                previousHeight != height);
        boolean shouldRepaint = sizeChanged || forceAdjustSize || (imageFilter != null);
        if (originalImage != null && shouldRepaint) {
            if (imageFilter == null) {
                imageFilter = this.imageFilter;
            }
            return applyFiltersAsync(width, height, imageFilter).thenAccept(v -> {
                previousWidth = (int) width;
                previousHeight = (int) height;
                forceAdjustSize = false;
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    private Image blur(double width, double height, Image img) {
        Image scaledInstance = img.getScaledInstance(Math.min(256, (int) width), Math.min(256, (int) height), Image.SCALE_FAST);
        return blur.filter(ImageUtil.buffer(scaledInstance), null).getScaledInstance((int) (width + (width / 5)), (int) (height + height / 5), Image.SCALE_FAST);
    }

    private CompletableFuture<Void> applyFiltersAsync(double width, double height, final ImageFilter imageFilter) {
        return CompletableFuture.runAsync(() -> {
            if (originalImage == null) {
                return;
            }
            try {
                Image scaledOriginal = ImageUtil.scaledImage(ImageUtil.bufferedCopy(originalImage), width, height);
                Image newImage = imageFilter.apply(scaledOriginal);
                Image newBackground = blur(width, height, scaledOriginal);
                newBackground = imageFilter.apply(newBackground);
                this.image = newImage;
                blurryBackgroundImage = newBackground;
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
            forceRedraw();
        }, executor);
    }

    private void forceRedraw() {
        invalidate();
        validate();
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public CompletableFuture<Void> setImage(Image image) {
        originalImage = image;
        forceAdjustSize = true;
        return adjustSize(getWidth(), getHeight());
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

    public CompletableFuture<Void> setImageFilter(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
        return adjustSize(getWidth(), getHeight(), imageFilter).thenAccept(v -> forceRedraw());
    }
}
