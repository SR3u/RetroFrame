package org.sr3u.photoframe.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupClickListener extends MouseAdapter {
    private final ImageWindow mainWindow;

    public PopupClickListener(ImageWindow mainWindow) {
        this.mainWindow = mainWindow;
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
        PopupMenu menu = new PopupMenu(mainWindow);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}