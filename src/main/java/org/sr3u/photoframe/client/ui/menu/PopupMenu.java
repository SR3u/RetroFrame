package org.sr3u.photoframe.client.ui.menu;

import org.sr3u.photoframe.client.ui.MouseReleaseListener;
import org.sr3u.photoframe.client.ui.main.ImageWindow;
import org.sr3u.photoframe.client.ui.settings.SettingsWindow;
import org.sr3u.photoframe.client.ui.settings.filters.FiltersWindow;
import org.sr3u.photoframe.server.Main;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {
    public PopupMenu(ImageWindow mainWindow) {
        addItem("Filters", e -> new FiltersWindow(mainWindow));
        addItem("Settings", e -> new SettingsWindow(Main.settings));
        if (mainWindow.isFullScreen()) {
            JMenuItem item = createItem("Exit FullScreen", e -> exitFullscreen(mainWindow));
            item.setAccelerator(ImageWindow.ALT_ENTER);
            add(item);
        } else {
            JMenuItem item = createItem("FullScreen", e -> enterFullscreen(mainWindow));
            item.setAccelerator(ImageWindow.ALT_ENTER);
            add(item);
        }
        addItem("About", e -> JOptionPane.showMessageDialog(null,
                "Authors:\n" +
                        "   SR3u    -- code\n" +
                        "   4113556 -- logo design, testing, and other help",
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
