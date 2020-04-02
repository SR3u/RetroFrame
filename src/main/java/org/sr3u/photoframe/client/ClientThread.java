package org.sr3u.photoframe.client;

import com.google.gson.Gson;
import org.sr3u.photoframe.server.Server;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientThread extends Thread {
    public static final Gson GSON = new Gson();
    private ImageWindow imageWindow = new ImageWindow();

    private static Socket clientSocket;
    private static InputStream in;
    private static BufferedWriter out;

    private final int port;
    private final String host;

    public ClientThread(String host, int port) {
        this.port = port;
        this.host = host;
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
                    int metadataSize = Server.intFromByteArray(in.readNBytes(4));
                    String json = new String(in.readNBytes(metadataSize));
                    Map<String, Object> metaData = GSON.fromJson(json, Map.class);
                    int imageSize = Server.intFromByteArray(in.readNBytes(4));
                    System.out.println(json);
                    imageWindow.displayImageAndMetadata(in, metaData);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
