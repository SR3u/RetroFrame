package org.sr3u.photoframe.client;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class PopupMenu extends JPopupMenu {
    public PopupMenu(ImageWindow mainWindow) {
        addItem("Settings", e -> JOptionPane.showMessageDialog(null,
                "Not implemented yet\n" +
                        "Please edit settings.properties file located in "+System.getProperty("user.dir")+"\n" +
                        "And then restart the app",
                "Settings",
                JOptionPane.ERROR_MESSAGE));
        if (mainWindow.isFullScreen()) {
            JMenuItem item = createItem("Exit FullScreen", e -> exitFullscreen(mainWindow));
            item.setMnemonic(KeyEvent.VK_ENTER);
            add(item);
        } else {
            JMenuItem item = createItem("FullScreen", e -> enterFullscreen(mainWindow));
            item.setMnemonic(KeyEvent.VK_ENTER);
            add(item);
        }
        addItem("About", e -> JOptionPane.showMessageDialog(null,
                "Authors:\n" +
                        "   SR3u    -- code\n" +
                        "   4113556 -- logo design",
                "About " + ImageWindow.TITLE_NAME,
                JOptionPane.INFORMATION_MESSAGE));
    }

    private void exitFullscreen(ImageWindow mainWindow) {
        mainWindow.exitFullScreen();
    }

    private void enterFullscreen(ImageWindow mainWindow) {
        mainWindow.enterFullScreen();
    }

    private JMenuItem addItem(String name, MouseReleaseListener mouseReleaseListener) {
        JMenuItem item = createItem(name, mouseReleaseListener);
        add(item);
        return item;
    }

    private JMenuItem createItem(String name, MouseReleaseListener mouseReleaseListener) {
        JMenuItem item = new JMenuItem(name);
        item.addMouseListener(mouseReleaseListener);
        return item;
    }
}
