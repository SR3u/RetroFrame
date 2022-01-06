package org.sr3u.retroframe.filters.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static Image scaledImage(Image image, Dimension newSize) {
        return scaledImage(image, newSize.width, newSize.height);
    }

    public static Image scaledImage(Image image, double width, double height) {
        return scaledImage(image, (int) width, (int) height);
    }

    public static Image scaledImage(Image image, int width, int height) {
        // Make sure the aspect ratio is maintained, so the image is not distorted
        Dimension size = scaledSize(image, width, height);

        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        if (imageHeight == size.height && imageWidth == size.width) {
            return image;
        }
        return newScaledImage(image, size);
    }

    public static BufferedImage newScaledImage(Image image, Dimension size) {
        // Draw the scaled image
        BufferedImage newImage = buffer(image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, size.width, size.height, null);

        return newImage;
    }

    public static Dimension scaledSize(Image image, int width, int height) {
        double thumbRatio = aspectRatio(width, height);
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double aspectRatio = aspectRatio(imageWidth, imageHeight);

        if (thumbRatio < aspectRatio) {
            height = (int) (width / aspectRatio);
        } else {
            width = (int) (height * aspectRatio);
        }
        return new Dimension(width, height);
    }

    public static double aspectRatio(double imageWidth, double imageHeight) {
        return imageWidth / imageHeight;
    }

    public static BufferedImage buffer(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        return bufferedCopy(img);
    }

    public static BufferedImage bufferedCopy(Image img) {
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static boolean isTall(Image image) {
        return aspectRatio(image.getWidth(null), image.getHeight(null)) < 1;
    }

    public static boolean isWide(Image image) {
        return aspectRatio(image.getWidth(null), image.getHeight(null)) < 1;
    }

    public static boolean isSquare(Image image) {
        return !isTall(image) && !isWide(image);
    }

    public static String googlePhotoSize(Dimension size) {
        return googlePhotoSize(size.width, size.height);
    }

    public static String googlePhotoSize(Number width, Number height) {
        return "=w" + width + "-h" + height;
    }
}