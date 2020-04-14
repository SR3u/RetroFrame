package org.sr3u.photoframe.client;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.server.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Getter
public class ImageWindow {
    public static final String TITLE_NAME = "Retro Frame ";
    public static final KeyStroke ALT_ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);
    ;
    private static final Object FULLSCREEN_ACTION = ImageWindow.class.getCanonicalName() + ".fullScreenAction";
    private final ImageFilter imageFilter;
    JFrame frame;
    ImagePanel imagePanel;
    OutlineLabel metadataLabel;
    private static final Logger log = LogManager.getLogger(ImageWindow.class);
    private boolean fullScreen = false;
    private AbstractAction fullScreenAction;

    public ImageWindow(boolean fullScreen, ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
        createComponents(fullScreen);
    }

    private void createComponents(boolean fullScreen) {
        frame = new JFrame();
        setIcon();
        if (imagePanel == null) {
            imagePanel = new ImagePanel(imageFilter);
        }
        handleTransparency();
        //frame.setLayout(new GridLayout(1, 1, 0, 0));
        frame.setLayout(new BorderLayout());
        frame.setSize(320, 240);
        this.fullScreen = fullScreen;
        if (fullScreen) {
            fullScreen();
        }
        imagePanel.setLayout(new BorderLayout());
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        metadataLabel = new OutlineLabel("Waiting for server...");
        imagePanel.add(metadataLabel, BorderLayout.SOUTH);
        frame.setTitle(TITLE_NAME);
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
        metadataLabel.setVisible(Main.settings.getClient().isShowMetadata());
        PopupClickListener popupClickListener = new PopupClickListener(this);
        fullScreenAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen(!isFullScreen());
            }
        };
        for (Component component : frame.getComponents()) {
            component.addMouseListener(popupClickListener);
        }
        imagePanel.getActionMap().put(FULLSCREEN_ACTION, fullScreenAction);
        imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ALT_ENTER, FULLSCREEN_ACTION);
    }

    private void setIcon() {
        URL url = ClassLoader.getSystemResource("org/sr3u/photoframe/client/icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        frame.setIconImage(img);
        //this is new since JDK 9
        final Taskbar taskbar = Taskbar.getTaskbar();
        try {
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(img);
        } catch (UnsupportedOperationException e) {
            log.error("The os does not support: 'Taskbar.setIconImage'");
        } catch (SecurityException e) {
            log.error("There was a security exception for: 'Taskbar.setIconImage'");
        }
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
                e.printStackTrace();
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
                if (metadataLabel.isVisible()) {
                    metadataLabel.setText(metaDataRendered);
                } else {
                    metadataLabel.setText("");
                }
                frame.setTitle(TITLE_NAME + metaDataRendered);
                frame.invalidate();
                frame.validate();
                frame.repaint();
            });
        }
    }

    private static <T> Optional<T> extract(@SuppressWarnings("rawtypes") Map map, Object key, Class<T> clazz) {
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
    private void fullScreen() {
        Preconditions.checkNotNull(frame);
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class[] params = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, frame, true);
        } catch (ClassNotFoundException e1) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            frame.setVisible(true);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public void enterFullScreen() {
        toggleFullScreen(true);
    }

    public void exitFullScreen() {
        toggleFullScreen(false);
    }

    private void toggleFullScreen(boolean fullScreen) {
        JFrame frame = this.frame;
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String title = frame.getTitle();
        createComponents(fullScreen);
        this.frame.setTitle(title);
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
