package org.sr3u.retroframe.settings;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.sr3u.retroframe.client.filters.ImageFilters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Value
@Builder
public class Settings implements Fillable {
    @Builder.Default
    @PropertyMap("java.awt.headless")
    boolean java_awt_headless = false;
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

    public void save(String fileName) {
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

    public Map<String, Class<?>> getPropertiesClasses() {
        Map<String, Class<?>> properties = this.toPropertiesClasses();
        properties.putAll(this.server.toPropertiesClasses());
        properties.putAll(this.client.toPropertiesClasses());
        properties.putAll(this.media.toPropertiesClasses());
        return properties;
    }

    public static Settings load(Properties properties) {
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

        @SuppressWarnings("ConstantConditions")
        public boolean isBackup() {
            return backup && backupPath != null;
        }

        public static Media load(Properties properties) {
            Media build = builder().build();
            build.fill(properties);
            return build;
        }
    }

    @Data
    @Builder
    public static class Client implements Fillable {
        public static final String CLIENT_IMAGE_FILTER_CHAIN_ALIAS = "client.imageFilterChain.alias.";
        @Builder.Default
        @PropertyMap("client.enable")
        boolean enable = true;
        @Builder.Default
        @PropertyMap("client.fullscreen")
        boolean fullScreen = false;
        @Builder.Default
        @PropertyMap("client.refreshDelay")
        int refreshDelay = 60000;
        @Builder.Default
        @PropertyMap("client.imageFilterChain")
        String imageFitlerChain = "Identity";
        @Builder.Default
        @PropertyMap("client.window.transparent")
        boolean transparentWindnow = false;
        @Builder.Default
        @PropertyMap("client.window.transparent.controls")
        boolean transparentWindnowControls = true;
        @Builder.Default
        @PropertyMap("client.filters.colorCacheSize")
        int colorCacheSize = 1024;
        @Builder.Default
        @PropertyMap("client.server.port")
        int serverPort = 4242;
        @Builder.Default
        @PropertyMap("client.server.address")
        String serverAddress = "localhost";
        @Builder.Default
        @PropertyMap("client.showMetadata")
        boolean showMetadata = true;

        public static Client load(Properties properties) {
            Client build = builder().build();
            properties.keySet().stream()
                    .filter(k -> k instanceof String)
                    .map(k -> (String) k)
                    .filter(k -> k.startsWith(CLIENT_IMAGE_FILTER_CHAIN_ALIAS))
                    .forEach(k -> {
                        String aliasName = k.substring(CLIENT_IMAGE_FILTER_CHAIN_ALIAS.length());
                        String aliasChain = properties.getProperty(k);
                        ImageFilters.INSTANCE.addAlias(aliasName, aliasChain);
                    });
            build.fill(properties);
            return build;
        }
    }

    @Value
    @Builder
    public static class Server implements Fillable {
        @Builder.Default
        @PropertyMap("server.enable")
        boolean enabled = true;
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
