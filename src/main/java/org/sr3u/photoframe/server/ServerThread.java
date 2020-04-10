package org.sr3u.photoframe.server;

import org.apache.log4j.Logger;
import org.sr3u.photoframe.server.data.ImageWithMetadata;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    private static final Logger log = Logger.getLogger(ServerThread.class);

    private final int port;
    private Repository repository;
    private boolean run = true;

    public ServerThread(Repository repository, int port) {
        this.repository = repository;
        this.port = port;
    }

    @Override
    public void run() {

        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (run) {
            try {
                Socket clientSocket = server.accept();
                handleClient(clientSocket);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        BufferedReader in = null;
        PrintStream out = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());
            String sizeStr = readString(in);
            Dimension size = parseDimension(sizeStr);
            out.flush();
            ImageWithMetadata random = repository.getRandom(size);
            Main.sendMetadata(out, random);
            Main.sendImage(size, out, random);
            out.flush();

        } catch (Exception e) {
            log.error(e);
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                log.error(e);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    private String readString(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int ch = in.read();
            if (ch <= 0 || ch == '\n' || ch == '\r') {
                break;
            }
            sb.append((char) ch);
        }
        return sb.toString();
    }

    private Dimension parseDimension(String sizeStr) {
        if (sizeStr == null) {
            return new Dimension(0, 0);
        }
        String[] split = sizeStr.toLowerCase().split("x");
        return new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
