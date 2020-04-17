package org.sr3u.photoframe.client.ui;

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
    }

    protected void setIcon() {
        URL url = ClassLoader.getSystemResource("org/sr3u/photoframe/client/icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        frame.setIconImage(img);
        try {
            //this is new since JDK 9
            final Taskbar taskbar = Taskbar.getTaskbar();
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(img);
        } catch (UnsupportedOperationException e) {
            log.error("The os does not support: 'Taskbar.setIconImage'");
        } catch (SecurityException e) {
            log.error("There was a security exception for: 'Taskbar.setIconImage'");
        }
    }
}
