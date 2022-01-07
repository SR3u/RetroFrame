package org.sr3u.retroframe.client.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ClientWindow {
    private static final Logger log = LogManager.getLogger(ClientWindow.class);

    protected JFrame frame = new JFrame();

    public ClientWindow() {
        setIcon();
        centerWindow();
    }

    public void centerWindow() {
        frame.setLocation(centerPoint());
    }

    public static Point centerPoint() {
        return new Point(
                (int) Math.max(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().getX() - 200, 50),
                (int) Math.max(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().getY() - 200, 50));
    }

    protected void setIcon() {
        URL url = ClassLoader.getSystemResource("org/sr3u/retroframe/client/icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        frame.setIconImage(img);
        try {
            //this is new since JDK 9
            final Taskbar taskbar = Taskbar.getTaskbar();
            //set icon for macOS (and other systems which do support this method)
            taskbar.setIconImage(img);
        } catch (UnsupportedOperationException e) {
            log.error("The os does not support: 'Taskbar.setIconImage'");
        } catch (SecurityException e) {
            log.error("There was a security exception for: 'Taskbar.setIconImage'");
        }
    }
}
