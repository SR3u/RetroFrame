package org.sr3u.photoframe.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.j256.ormlite.logger.LocalLog;
import lombok.Builder;
import lombok.Data;
import org.sr3u.photoframe.client.ClientThread;
import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.events.EventSystem;
import org.sr3u.photoframe.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    public static final String DISPLAY_SERVERS_JSON = "displayServers.json";
    public static final Settings settings;
    private static final EventSystem eventsSystem;

    static { // HIDE DOCK ICON (if any)
        System.setProperty("com.j256.ormlite.logger.type", "ERROR");
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        settings = Settings.load("settings.properties");
        System.setProperty("java.awt.headless", String.valueOf(settings.isJava_awt_headless()));
        System.out.println("java.awt.headless: " + java.awt.GraphicsEnvironment.isHeadless());
        eventsSystem = new EventSystem();
    }

    private static final Type REVIEW_TYPE = new TypeToken<List<DisplayServer>>() {
    }.getType();

    private static List<DisplayServer> displayServers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            displayServers = readServersFromFile(DISPLAY_SERVERS_JSON);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            displayServers = Collections.emptyList();
        }
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty("com.j256.ormlite.*", "ERROR");
        System.setProperty("log4j.com.j256.ormlite.*", "ERROR");
        String credentialsPath = new File("credentials/credentials.json").getAbsolutePath();
        if (settings.getMedia().isBackup()) {
            eventsSystem.registerHandler(new MediaBackup());
        }
        try {
            PhotosLibraryClient client = PhotosLibraryClientFactory.createClient(credentialsPath, Resources.REQUIRED_SCOPES);
               /* InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = client.listMediaItems();
                for (MediaItem item : response.iterateAll()) {
                    // Get some properties of a media item
                    System.out.println(item);
                }*/
            Repository repository = new Repository(client, eventsSystem);
            scheduleRefresh(repository);
            scheduleSending(repository);
            new ServerThread(repository, settings.getServer().getPort()).start();
            if (settings.isClientEnabled()) {
                new ClientThread("localhost", settings.getServer().getPort()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scheduleSending(Repository repository) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            try {
                for (DisplayServer dServer : displayServers) {
                    try {
                        sendRandomPhoto(repository, dServer);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    private static void sendRandomPhoto(Repository repository, DisplayServer server) throws IOException {
        Socket socket = new Socket(server.getAddress(), server.getPort());
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.flush();
        ImageWithMetadata random = repository.getRandom(server.getScreenSize());
        sendMetadata(out, random);
        sendImage(server, out, random);
        socket.close();
    }

    public static void sendMetadata(PrintStream out, ImageWithMetadata random) throws IOException {
        String json = random.jsonMetadata();
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        out.write(intToByteArray(jsonBytes.length));
        out.flush();
        out.write(jsonBytes);
        out.flush();
    }

    private static void sendImage(DisplayServer server, PrintStream out, ImageWithMetadata random) throws IOException {
        sendImage(server.getScreenSize(), out, random);
    }

    public static void sendImage(Dimension size, PrintStream out, ImageWithMetadata random) throws IOException {
        BufferedImage image = ImageUtil.buffer(ImageUtil.scaledImage(random.getImage(), size));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        System.out.println("bytes.length = " + bytes.length);
        out.write(intToByteArray(bytes.length));
        out.flush();
        out.write(bytes);
        out.flush();
    }

    private static void scheduleRefresh(Repository repository) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(repository::refresh, 0, 1, TimeUnit.DAYS);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static int intFromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static List<DisplayServer> readServersFromFile(String filePath) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filePath));
        return gson.fromJson(reader, REVIEW_TYPE);
    }

    @Data
    @Builder
    public static class DisplayServer implements Serializable {
        @Builder.Default
        private String address = "localhost";
        @Builder.Default
        private int port = 16385;
        @Builder.Default
        private int width = 600;
        @Builder.Default
        private int height = 448;

        public Dimension getScreenSize() {
            return new Dimension(width, height);
        }
    }
}
