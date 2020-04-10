package org.sr3u.photoframe.client;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.server.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class ImageWindow {
    JFrame frame;
    ImagePanel imagePanel;
    OutlineLabel metadataLabel;
    private static final Logger log = Logger.getLogger(ImageWindow.class);

    public ImageWindow(boolean fullScreen, ImageFilter imageFilter) {
        frame = new JFrame();
        imagePanel = new ImagePanel(imageFilter);
        handleTransparency();
        //frame.setLayout(new GridLayout(1, 1, 0, 0));
        frame.setLayout(new BorderLayout());
        frame.setSize(320, 240);
        if (fullScreen) {
            enableOSXFullscreen(frame);
        }
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

    private void handleTransparency() {
        if (Main.settings.getClient().isTransparentWindnow()) {
            if (!Main.settings.getClient().isTransparentWindnowControls()) {
                frame.setUndecorated(true);
            } else {
                JFrame.setDefaultLookAndFeelDecorated(true);
                frame = new JFrame();
            }
            try {
                frame.setBackground(new Color(0, 0, 0, 0));
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            frame.setBackground(Color.WHITE);
        }
        imagePanel.setOpaque(false);
    }

    public Dimension getSize() {
        return frame.getSize();
    }

    public void displayImageAndMetadata(InputStream imgStream, Map<String, Object> metaData) throws Exception {
        BufferedImage img = ImageIO.read(new BufferedInputStream(imgStream));
        if (img == null) {
            log.error("Failed to receive image!");
        } else {
            imagePanel.setImage(img).thenAccept(v -> {
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
            });
        }
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void enableOSXFullscreen(JFrame window) {
        Preconditions.checkNotNull(window);
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (ClassNotFoundException e1) {
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
            window.setVisible(true);
        } catch (Exception e) {
            log.error(e);
        }
    }

}
