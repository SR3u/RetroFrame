package org.sr3u.photoframe.server;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.j256.ormlite.logger.LocalLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.client.ClientThread;
import org.sr3u.photoframe.misc.util.ImageUtil;
import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.events.EventSystem;
import org.sr3u.photoframe.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static final String DISPLAY_SERVERS_JSON = "displayServers.json";
    public static final Settings settings;
    private static final EventSystem eventsSystem;

    static { // HIDE DOCK ICON (if any)
        settings = Settings.load("settings.properties");
        System.setProperty("com.j256.ormlite.logger.type", "ERROR");
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty("java.awt.headless", String.valueOf(settings.isJava_awt_headless()));
        log.info("java.awt.headless: " + java.awt.GraphicsEnvironment.isHeadless());
        eventsSystem = new EventSystem();
    }

    public static void main(String[] args) {
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty("com.j256.ormlite.*", "ERROR");
        System.setProperty("log4j.com.j256.ormlite.*", "ERROR");
        String credentialsPath = new File("credentials/credentials.json").getAbsolutePath();
        if (settings.getMedia().isBackup()) {
            eventsSystem.registerHandler(new MediaBackup());
        }
        try {
            if (settings.getServer().isEnabled()) {
                PhotosLibraryClient client = PhotosLibraryClientFactory.createClient(credentialsPath, Resources.REQUIRED_SCOPES);
               /* InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = client.listMediaItems();
                for (MediaItem item : response.iterateAll()) {
                    // Get some properties of a media item
                    log.info(item);
                }*/
                Repository repository = new Repository(client, eventsSystem);
                scheduleRefresh(repository);
                new ServerThread(repository, settings.getServer().getPort()).start();
            }
            if (settings.isClientEnabled()) {
                new ClientThread(settings.getClient().getServerAddress(), settings.getClient().getServerPort()).start();
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public static void sendMetadata(PrintStream out, ImageWithMetadata random) throws IOException {
        String json = random.jsonMetadata();
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        out.write(intToByteArray(jsonBytes.length));
        out.flush();
        out.write(jsonBytes);
        out.flush();
    }

    public static void sendImage(Dimension size, PrintStream out, ImageWithMetadata random) throws IOException {
        BufferedImage image = ImageUtil.buffer(ImageUtil.scaledImage(random.getImage(), size));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        log.info("bytes.length = " + bytes.length);
        out.write(intToByteArray(bytes.length));
        out.flush();
        out.write(bytes);
        out.flush();
    }

    private static void scheduleRefresh(Repository repository) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(repository::refresh, 0, 1, TimeUnit.DAYS);
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static int intFromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

}
