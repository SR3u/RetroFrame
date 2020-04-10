package org.sr3u.photoframe.server;

import com.google.gson.Gson;
import com.google.photos.library.v1.proto.MediaItem;
import org.apache.log4j.Logger;
import org.sr3u.photoframe.server.data.Item;
import org.sr3u.photoframe.server.data.MediaType;
import org.sr3u.photoframe.server.events.DeletedItemEvent;
import org.sr3u.photoframe.server.events.Event;
import org.sr3u.photoframe.server.events.NewItemEvent;
import org.sr3u.photoframe.server.events.UpdatedItemEvent;
import sr3u.streamz.functionals.Consumerex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MediaBackup implements Consumerex<Event> {

    private static final Logger log = Logger.getLogger(Repository.class);

    public static final String DELETED = "deleted";
    private static final String ITEMS = "items";
    private static final Gson GSON = new Gson();
    DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void accept(Event e) throws Exception {
        String backupPath = Main.settings.getMedia().getBackupPath();
        e.ifType(NewItemEvent.class, event -> saveIfNeeded(backupPath, event))
                .ifType(UpdatedItemEvent.class, event -> saveIfNeeded(backupPath, event))
                .ifType(DeletedItemEvent.class, event -> {
                    moveToTrash(backupPath, event);
                });
    }

    private void moveToTrash(String backupPath, DeletedItemEvent event) throws IOException {
        moveMediaToTrash(backupPath, event);
        moveMetadataToTrash(backupPath, event);
    }

    private void moveMetadataToTrash(String backupPath, DeletedItemEvent event) throws IOException {
        Item item = event.getItem();
        Path fullFolderPath = getFullFolderPath(backupPath, item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName() + ".json");
        Path fullTrashFolderPath = getTrashFolderPath(backupPath, item);
        Path trashFilePath = Paths.get(fullTrashFolderPath.toString(), item.getFileName() + ".json");
        Files.move(filePath, trashFilePath);
    }

    private void moveMediaToTrash(String backupPath, DeletedItemEvent event) throws IOException {
        Item item = event.getItem();
        Path fullFolderPath = getFullFolderPath(backupPath, item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Path fullTrashFolderPath = getTrashFolderPath(backupPath, item);
        Path trashFilePath = Paths.get(fullTrashFolderPath.toString(), item.getFileName());
        Files.move(filePath, trashFilePath);
    }

    private Path getTrashFolderPath(String backupPath, Item item) throws IOException {
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path fullTrashFolderPath = Paths.get(backupPath, DELETED, dateStr, item.getGoogleID());
        Files.createDirectories(fullTrashFolderPath);
        return fullTrashFolderPath;
    }

    private void saveIfNeeded(String backupPath, Event event) throws IOException {
        saveItemFile(event, backupPath);
        saveMetadataFile(event, backupPath);
    }

    private void saveMetadataFile(Event event, String backupPath) throws IOException {
        File metadataFile = getMetadataFile(backupPath, event.getItem());
        String newJson = GSON.toJson(event.getMediaItem().getMediaMetadata());
        String oldJson = "";
        if (metadataFile.exists()) {
            oldJson = Files.readString(Path.of(metadataFile.getAbsolutePath()), StandardCharsets.UTF_8);
        }
        if (!Objects.equals(oldJson, newJson)) {
            FileWriter fileWriter = new FileWriter(metadataFile, StandardCharsets.UTF_8);
            fileWriter.write(newJson);
            fileWriter.flush();
            log.info("Saved item metadata " + metadataFile.getAbsolutePath());
        }
    }

    private File getMetadataFile(String backupPath, Item item) throws IOException {
        Path fullFolderPath = getFullFolderPath(backupPath, item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName() + ".json");
        Files.createDirectories(fullFolderPath);
        return new File(filePath.toAbsolutePath().toString());
    }

    private File getItemFile(String backupPath, Item item) throws IOException {
        Path fullFolderPath = getFullFolderPath(backupPath, item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Files.createDirectories(fullFolderPath);
        return new File(filePath.toAbsolutePath().toString());
    }

    private Path getFullFolderPath(String backupPath, Item item) throws IOException {
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path path = Paths.get(backupPath, ITEMS, dateStr, item.getGoogleID());
        Files.createDirectories(path);
        return path;
    }

    private void saveItemFile(Event event, String backupPath) throws IOException {
        Item item = event.getItem();
        File file = getItemFile(backupPath, item);
        if (!file.exists()) {
            MediaItem mediaItem = event.getMediaItem();
            String itemUrlString = mediaItem.getBaseUrl();
            if (item.getMediaType() == MediaType.IMAGE) {
                itemUrlString += "=d";
            } else if (item.getMediaType() == MediaType.VIDEO) {
                itemUrlString += "=dv";
            }
            URL itemUrl = new URL(itemUrlString);
            ReadableByteChannel rbc = Channels.newChannel(itemUrl.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            log.info("Saved item " + file.getAbsolutePath());
        } else {
            log.info("Item already saved " + file.getAbsolutePath());
        }
    }
}
