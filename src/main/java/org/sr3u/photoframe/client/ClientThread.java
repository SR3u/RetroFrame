package org.sr3u.photoframe.client;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.sr3u.photoframe.client.filters.Identity;
import org.sr3u.photoframe.client.filters.ImageFilter;
import org.sr3u.photoframe.client.filters.ImageFilters;
import org.sr3u.photoframe.server.Main;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

public class ClientThread extends Thread {

    private static final Logger log = Logger.getLogger(ClientThread.class);

    public static final Gson GSON = new Gson();
    private ImageWindow imageWindow;

    private static Socket clientSocket;
    private static InputStream in;
    private static BufferedWriter out;

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
        imageWindow = new ImageWindow(Main.settings.getClient().isFullScreen(), imageFilter);
    }

    @Override
    public void run() {
        while (true) {
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
                    Map<String, Object> metaData = GSON.fromJson(json, Map.class);
                    int imageSize = Main.intFromByteArray(in.readNBytes(4));
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
                    Thread.sleep(Main.settings.getClient().getRefreshDelay());
                }
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
        }
    }
}
