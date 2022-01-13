package org.sr3u.retroframe.client.ui.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.retroframe.client.RetroframeClient;
import org.sr3u.retroframe.client.ui.main.ImageWindow;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PopupClickListener extends MouseAdapter {
    @SuppressWarnings("FieldCanBeLocal")
    private final ImageWindow mainWindow;
    private final CompletableFuture<RetroframeClient> clientThread;

    private static final Logger log = LogManager.getLogger(PopupClickListener.class);

    public PopupClickListener(ImageWindow mainWindow, CompletableFuture<RetroframeClient> clientThread) {
        this.mainWindow = mainWindow;
        this.clientThread = clientThread;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            doPop(e);
        }
    }

    private void doPop(MouseEvent e) {
        try {
            RetroframeClient retroframeClient = this.clientThread.get();
            PopupMenu menu = new PopupMenu(mainWindow, retroframeClient);
            menu.show(e.getComponent(), e.getX(), e.getY());
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            log.error(e);
        }
    }
}