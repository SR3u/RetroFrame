package org.sr3u.photoframe.settings;

import lombok.Builder;
import lombok.Value;

import java.io.*;
import java.util.Properties;

@Value
@Builder
public class Settings implements Fillable {
    @Builder.Default
    @PropertyMap("java.awt.headless")
    boolean java_awt_headless = true;
    @Builder.Default
    @PropertyMap("processingThreads")
    int processingTreads = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
    @Builder.Default
    Client client = Client.builder().build();
    @Builder.Default
    Server server = Server.builder().build();
    @Builder.Default
    Media media = Media.builder().build();

    public static Settings load(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            Settings defaultSettings = builder().build();
            defaultSettings.save(fileName);
            return defaultSettings;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return load(properties);
    }

    private void save(String fileName) {
        Properties properties = getProperties();
        try {
            properties.store(new FileOutputStream(new File(fileName)), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getProperties() {
        Properties properties = this.toProperties();
        properties.putAll(this.server.toProperties());
        properties.putAll(this.client.toProperties());
        properties.putAll(this.media.toProperties());
        return properties;
    }

    private static Settings load(Properties properties) {
        SettingsBuilder builder = Settings.builder();
        builder.server(Server.load(properties));
        builder.client(Client.load(properties));
        builder.media(Media.load(properties));
        Settings settings = builder.build();
        settings.fill(properties);
        return settings;
    }

    public boolean isClientEnabled() {
        return client.enable && (!java_awt_headless);
    }

    @Value
    @Builder
    public static class Media implements Fillable {
        @Builder.Default
        @PropertyMap("media.backup")
        boolean backup = false;
        @PropertyMap("media.backupPath")
        @Builder.Default
        String backupPath = null;
        @Builder.Default
        @PropertyMap("media.databasePath")
        String databasePath = "mediaItems.db";
        @Builder.Default
        @PropertyMap("media.albumName")
        String albumName = "";
        @Builder.Default
        @PropertyMap("media.mediaItemExpiryTime")
        public long mediaItemExpiryTime = 3000;

        public boolean isBackup() {
            return backup && backupPath != null;
        }

        public static Media load(Properties properties) {
            Media build = builder().build();
            build.fill(properties);
            return build;
        }
    }

    @Value
    @Builder
    public static class Client implements Fillable {
        @Builder.Default
        @PropertyMap("client.enable")
        boolean enable = false;
        @Builder.Default
        @PropertyMap("client.fullscreen")
        boolean fullScreen = false;
        @Builder.Default
        @PropertyMap("client.refreshDelay")
        int refreshDelay = 60000;
        @Builder.Default
        @PropertyMap("client.imageFilterChain")
        String imageFitlerChain = "Identity";

        public static Client load(Properties properties) {
            Client build = builder().build();
            build.fill(properties);
            return build;
        }
    }

    @Value
    @Builder
    public static class Server implements Fillable {
        @Builder.Default
        @PropertyMap("server.port")
        int port = 4242;

        public static Server load(Properties properties) {
            Server build = builder().build();
            build.fill(properties);
            return build;
        }
    }

}
