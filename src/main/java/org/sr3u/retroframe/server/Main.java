package org.sr3u.retroframe.server;

import com.google.photos.library.v1.PhotosLibraryClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.retroframe.client.ClientThread;
import org.sr3u.retroframe.filters.utils.ImageUtil;
import org.sr3u.retroframe.server.data.ImageWithMetadata;
import org.sr3u.retroframe.server.events.EventSystem;
import org.sr3u.retroframe.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static final Settings settings;
    private static final EventSystem eventsSystem;

    public static final String SETTINGS_PROPERTIES = "settings.properties";

    static { // HIDE DOCK ICON (if any)
        settings = Settings.load(SETTINGS_PROPERTIES);
        System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        System.setProperty("java.awt.headless", String.valueOf(settings.isJava_awt_headless()));
        log.info("java.awt.headless: " + java.awt.GraphicsEnvironment.isHeadless());
        eventsSystem = new EventSystem();
    }

    public static void main(String[] args) {
        System.setProperty("com.j256.ormlite.logger.level", "ERROR");
        System.setProperty("com.j256.ormlite.*", "ERROR");
        System.setProperty("log4j.com.j256.ormlite.*", "ERROR");
        System.setProperty("org.sr3u.retroframe.colorCacheSize", String.valueOf(settings.getClient().getColorCacheSize()));
        String credentialsPath = new File("credentials/credentials.json").getAbsolutePath();
        MediaBackup mediaBackup = null;
        if (settings.getMedia().isBackup()) {
            mediaBackup = new MediaBackup();
            eventsSystem.registerHandler(mediaBackup);
        }
        try {
            if (settings.getServer().isEnabled()) {
                PhotosLibraryClient client = PhotosLibraryClientFactory.createClient(credentialsPath, Resources.REQUIRED_SCOPES);
                Repository repository = new Repository(client, eventsSystem);
                repository.setMediaBackupRepository(mediaBackup);
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

    public static void sendMetadata(PrintStream out, ImageWithMetadata random) {
        String json = random.jsonMetadata();
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        byte[] buf = intToByteArray(jsonBytes.length);
        out.write(buf, 0, buf.length);
        out.flush();
        out.write(jsonBytes, 0, jsonBytes.length);
        out.flush();
    }

    public static void sendImage(Dimension size, PrintStream out, ImageWithMetadata random) throws IOException {
        BufferedImage image = ImageUtil.buffer(ImageUtil.scaledImage(random.getImage(), size));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        log.info("bytes.length = " + bytes.length);
        byte[] buf = intToByteArray(bytes.length);
        out.write(buf, 0, buf.length);
        out.flush();
        out.write(bytes, 0, bytes.length);
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

    public static void restartApplication() throws IOException, URISyntaxException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            return;
        }

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }

}
