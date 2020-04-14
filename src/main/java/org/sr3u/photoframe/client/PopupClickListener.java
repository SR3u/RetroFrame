package org.sr3u.photoframe.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupClickListener extends MouseAdapter {
    private final ImageWindow mainWindow;
    private final PopupMenu menu;

    public PopupClickListener(ImageWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.menu = new PopupMenu(mainWindow);
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger())
            doPop(e);
    }

    private void doPop(MouseEvent e) {
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}