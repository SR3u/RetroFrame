package org.sr3u.retroframe.client.ui.menu;

import org.sr3u.retroframe.client.RetroframeClient;
import org.sr3u.retroframe.client.ui.MouseReleaseListener;
import org.sr3u.retroframe.client.ui.main.ImageWindow;
import org.sr3u.retroframe.client.ui.settings.SettingsWindow;
import org.sr3u.retroframe.client.ui.settings.filters.FiltersWindow;
import org.sr3u.retroframe.server.Main;

import javax.swing.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PopupMenu extends JPopupMenu {

    private final Executor async = Executors.newSingleThreadScheduledExecutor();

    public PopupMenu(ImageWindow mainWindow, RetroframeClient retroframeClient) {
        addItem("Next", e -> async.execute(retroframeClient::updateImageOnce));
        addItem("Filters", e -> new FiltersWindow(mainWindow));
        addItem("Settings", e -> new SettingsWindow(Main.settings));
        JMenuItem item;
        if (mainWindow.isFullScreen()) {
            item = createItem("Exit FullScreen", e -> exitFullscreen(mainWindow));
        } else {
            item = createItem("FullScreen", e -> enterFullscreen(mainWindow));
        }
        item.setAccelerator(ImageWindow.ALT_ENTER);
        add(item);
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

    private void addItem(String name, MouseReleaseListener mouseReleaseListener) {
        JMenuItem item = createItem(name, mouseReleaseListener);
        add(item);
    }

    private JMenuItem createItem(String name, MouseReleaseListener mouseReleaseListener) {
        JMenuItem item = new JMenuItem(name);
        item.addMouseListener(mouseReleaseListener);
        return item;
    }
}
