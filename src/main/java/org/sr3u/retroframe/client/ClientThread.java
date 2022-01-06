package org.sr3u.retroframe.client;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.retroframe.client.filters.Identity;
import org.sr3u.retroframe.client.filters.ImageFilter;
import org.sr3u.retroframe.client.filters.ImageFilters;
import org.sr3u.retroframe.client.ui.main.ImageWindow;
import org.sr3u.retroframe.server.Main;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientThread extends Thread {

    private static final Logger log = LogManager.getLogger(ClientThread.class);

    public static final Gson GSON = new Gson();

    private final ScheduledExecutorService execService = Executors.newSingleThreadScheduledExecutor();
    private final ImageWindow imageWindow;

    private Socket clientSocket;
    private InputStream in;
    private BufferedWriter out;

    private final int port;
    private final String host;

    public ClientThread(String host, int port) {
        this.port = port;
        this.host = host;
        ImageFilter imageFilter;
        try {
            imageFilter = ImageFilters.parse(Main.settings.getClient().getImageFitlerChain());
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            imageFilter = new Identity();
        }
        imageWindow = new ImageWindow(Main.settings.getClient().isFullScreen(), imageFilter, this);
    }

    @Override
    public void run() {
        updateImageAndSchedule();
    }

    private void scheduleRefresh() {
        execService.schedule(this::updateImageAndSchedule, Main.settings.getClient().getRefreshDelay(), TimeUnit.MILLISECONDS);
    }

    private void updateImageAndSchedule() {
        updateImage(true);
    }

    public void updateImageOnce() {
        updateImage(false);
    }

    private synchronized void updateImage(boolean schedule) {
        try {
            try {
                clientSocket = new Socket(host, port);
                in = new BufferedInputStream(clientSocket.getInputStream());
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.flush();
                Dimension size = imageWindow.getSize();
                out.write(size.width + "x" + size.height + "x" + 32);
                out.write(0);
                out.flush();
                int metadataSize = Main.intFromByteArray(in.readNBytes(4));
                String json = new String(in.readNBytes(metadataSize));
                Map<String, Object> metaData = parseMetadata(json);
                int imageSize = Main.intFromByteArray(in.readNBytes(4));
                metaData.put("mem_size", imageSize);
                log.info("Metadata: " + json);
                imageWindow.displayImageAndMetadata(in, metaData);
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    log.error(e);
                    e.printStackTrace();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    log.error(e);
                    e.printStackTrace();
                }
                try {
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    log.error(e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        } finally {
            if (schedule) {
                scheduleRefresh();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMetadata(String json) {
        return GSON.fromJson(json, Map.class);
    }
}
