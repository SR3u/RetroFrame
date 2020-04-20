package org.sr3u.photoframe.client.ui.main;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.client.ClientThread;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.ui.ClientWindow;
import org.sr3u.photoframe.client.ui.menu.PopupClickListener;
import org.sr3u.photoframe.server.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Getter
public class ImageWindow extends ClientWindow {
    public static final String TITLE_NAME = "Retro Frame ";
    public static final KeyStroke ALT_ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);
    private static final Object FULLSCREEN_ACTION = ImageWindow.class.getCanonicalName() + ".fullScreenAction";
    private final ClientThread clientThread;
    private ImageFilter imageFilter;
    ImagePanel imagePanel;
    OutlineLabel metadataLabel;
    private static final Logger log = LogManager.getLogger(ImageWindow.class);
    private boolean fullScreen = false;
    private AbstractAction fullScreenAction;

    private Dimension regularSize = new Dimension(320, 240);
    private Point regularLocation = centerPoint();

    public ImageWindow(boolean fullScreen, ImageFilter imageFilter, ClientThread clientThread) {
        super();
        this.imageFilter = imageFilter;
        this.clientThread = clientThread;
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
        frame.setSize(regularSize);
        frame.setLocation(regularLocation);
        this.fullScreen = fullScreen;
        if (fullScreen) {
            fullScreen();
        }
        imagePanel.setLayout(new BorderLayout());
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (metadataLabel == null) {
            metadataLabel = new OutlineLabel("Waiting for server...");
        }
        imagePanel.add(metadataLabel, BorderLayout.SOUTH);
        frame.setTitle(TITLE_NAME);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                if (!isFullScreen()) {
                    regularSize = frame.getSize();
                    regularLocation = frame.getLocation();
                }
                frame.repaint();
                imagePanel.setSize(frame.getSize());
                forceRedraw();
            }

            @Override
            public void componentMoved(ComponentEvent evt) {
                if (!isFullScreen()) {
                    regularSize = frame.getSize();
                    regularLocation = frame.getLocation();
                }
            }
        });
        metadataLabel.setVisible(Main.settings.getClient().isShowMetadata());
        PopupClickListener popupClickListener = new PopupClickListener(this, clientThread);
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
                forceRedraw();
            });
        }
    }

    private void forceRedraw() {
        frame.invalidate();
        frame.validate();
        frame.repaint();
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
        JFrame oldFrame = this.frame;
        oldFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String title = oldFrame.getTitle();
        createComponents(fullScreen);
        this.frame.setTitle(title);
        forceRedraw();
        oldFrame.dispatchEvent(new WindowEvent(oldFrame, WindowEvent.WINDOW_CLOSING));
    }

    public void setImageFilter(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
        this.imagePanel.setImageFilter(imageFilter).thenAccept(v -> forceRedraw());
    }
}
