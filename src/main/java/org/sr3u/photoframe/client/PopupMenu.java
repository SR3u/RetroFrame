package org.sr3u.photoframe.client;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {
    public PopupMenu(ImageWindow mainWindow) {
        addItem("Settings", e -> JOptionPane.showMessageDialog(null,
                "Not implemented yet\n" +
                        "Please edit settings.properties file located in "+System.getProperty("user.dir")+"\n" +
                        "And then restart the app",
                "Settings",
                JOptionPane.ERROR_MESSAGE));
        if (mainWindow.isFullScreen()) {
            addItem("Exit FullScreen", e -> exitFullscreen(mainWindow));
        } else {
            addItem("FullScreen", e -> enterFullscreen(mainWindow));
        }
        addItem("About", e -> JOptionPane.showMessageDialog(null,
                "Created by SR3u and 4113556",
                "About " + ImageWindow.TITLE_NAME,
                JOptionPane.INFORMATION_MESSAGE));
    }

    private void exitFullscreen(ImageWindow mainWindow) {
        mainWindow.exitFullScreen();
    }

    private void enterFullscreen(ImageWindow mainWindow) {
        mainWindow.enterFullScreen();
    }

    private void addItem(String name, MouseReleaseListener mouseReleaseListener) {
        JMenuItem settingsItem = new JMenuItem(name);
        settingsItem.addMouseListener(mouseReleaseListener);
        add(settingsItem);
    }
}
