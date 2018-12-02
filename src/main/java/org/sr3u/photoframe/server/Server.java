package org.sr3u.photoframe.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.j256.ormlite.logger.LocalLog;
import lombok.Builder;
import lombok.Data;
import org.sr3u.photoframe.server.data.ImageWithMetadata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    static { // HIDE DOCK ICON (if any)
        System.setProperty("java.awt.headless", "true");
        System.out.println("java.awt.headless: " + java.awt.GraphicsEnvironment.isHeadless());
    }

    private static final Type REVIEW_TYPE = new TypeToken<List<DisplayServer>>() {
    }.getType();

    private static List<DisplayServer> displayServers = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
        displayServers = readServersFromFile("displayServers.json");
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty("com.j256.ormlite.*", "ERROR");
        System.setProperty("log4j.com.j256.ormlite.*", "ERROR");
        String credentialsPath = new File("credentials/credentials.json").getAbsolutePath();
        try {
            PhotosLibraryClient client = PhotosLibraryClientFactory.createClient(credentialsPath, Resources.REQUIRED_SCOPES);
               /* InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = client.listMediaItems();
                for (MediaItem item : response.iterateAll()) {
                    // Get some properties of a media item
                    System.out.println(item);
                }*/
            Repository repository = new Repository(client);
            scheduleRefresh(repository);
            scheduleSending(repository);


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
        ImageWithMetadata random = repository.getRandom();
        sendMetadata(out, random);
        sendImage(server, out, random);
        socket.close();
    }

    private static void sendMetadata(PrintStream out, ImageWithMetadata random) throws IOException {
        String json = random.jsonMetadata();
        byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
        out.write(intToByteArray(jsonBytes.length));
        out.flush();
        out.write(jsonBytes);
        out.flush();
    }

    private static void sendImage(DisplayServer server, PrintStream out, ImageWithMetadata random) throws IOException {
        BufferedImage image = ImageUtil.scaledImage(random.getImage(), server.getScreenSize());
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
