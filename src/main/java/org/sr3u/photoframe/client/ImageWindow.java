package org.sr3u.photoframe.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class ImageWindow {
    JFrame frame;
    ImagePanel imagePanel;
    OutlineLabel metadataLabel;

    public ImageWindow() {
        frame = new JFrame();
        //frame.setLayout(new GridLayout(1, 1, 0, 0));
        frame.setLayout(new BorderLayout());
        frame.setSize(320, 240);
        imagePanel = new ImagePanel();
        imagePanel.setLayout(new BorderLayout());
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        metadataLabel = new OutlineLabel("Waiting for server...");
        imagePanel.add(metadataLabel, BorderLayout.SOUTH);
        frame.setTitle("Photo Frame");
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                frame.repaint();
                imagePanel.setSize(frame.getSize());
                frame.invalidate();
                frame.validate();
                frame.repaint();
            }
        });
    }

    public Dimension getSize() {
        return frame.getSize();
    }

    public void displayImageAndMetadata(InputStream imgStream, Map<String, Object> metaData) throws IOException {
        BufferedImage img = ImageIO.read(new BufferedInputStream(imgStream));
        if (img == null) {
            System.out.println("Failed to receive image!");
        } else {
            imagePanel.setImage(img);
        }
        String metaDataRendered = extract(metaData, "creationTime_", Map.class)
                .flatMap(md -> extract(md, "seconds", Number.class))
                .map(n -> multiply(n, 1000))
                .map(Number::longValue)
                .map(Date::new)
                .map(d -> new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(d))
                .orElse("??.??.???? ??:??:??");
        metadataLabel.setText(metaDataRendered);
        frame.setTitle("Photo Frame " + metaDataRendered);
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    private static <T> Optional<T> extract(Map map, Object key, Class<T> clazz) {
        if (map.get(key) != null && clazz.isInstance(map.get(key))) {
            return Optional.of(clazz.cast(map.get(key)));
        }
        return Optional.empty();
    }

    public static Number multiply(Number number, int multiplier) {
        return new Number() {
            @Override
            public long longValue() {
                return number.longValue() * multiplier;
            }

            @Override
            public int intValue() {
                return number.intValue() * multiplier;
            }

            @Override
            public float floatValue() {
                return number.floatValue() * multiplier;
            }

            @Override
            public double doubleValue() {
                return number.doubleValue() * multiplier;
            }
        };
    }
}
