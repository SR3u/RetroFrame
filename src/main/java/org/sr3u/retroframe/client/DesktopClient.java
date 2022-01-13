package org.sr3u.retroframe.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.retroframe.filters.Identity;
import org.sr3u.retroframe.filters.ImageFilter;
import org.sr3u.retroframe.filters.ImageFilters;
import org.sr3u.retroframe.client.ui.main.ImageWindow;
import org.sr3u.retroframe.server.Main;

import java.util.concurrent.CompletableFuture;

public class DesktopClient {

    private static final Logger log = LogManager.getLogger(DesktopClient.class);
    private final RetroframeClient retroframeClient;


    public DesktopClient(String host, int port) {
        ImageFilter imageFilter;
        try {
            imageFilter = ImageFilters.parse(Main.settings.getClient().getImageFitlerChain());
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            imageFilter = new Identity();
        }
        CompletableFuture<RetroframeClient> clientThreadCf = new CompletableFuture<>();
        retroframeClient = new RetroframeClient(host, port, new ImageWindow(Main.settings.getClient().isFullScreen(), imageFilter, clientThreadCf));
        clientThreadCf.complete(retroframeClient);
    }

    public void start() {
        retroframeClient.start();
    }

}
