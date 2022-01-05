package org.sr3u.photoframe.server;

import com.google.gson.Gson;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.misc.util.ImageUtil;
import org.sr3u.photoframe.server.data.ImageWithMetadata;
import org.sr3u.photoframe.server.data.Item;
import org.sr3u.photoframe.server.data.MediaType;
import org.sr3u.photoframe.server.events.Event;
import org.sr3u.photoframe.server.events.*;
import sr3u.streamz.functionals.Consumerex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
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

public class MediaBackup implements Consumerex<Event>, MediaBackupRepository {

    private static final Logger log = LogManager.getLogger(Repository.class);

    public static final String DELETED = "deleted";
    private static final String ITEMS = "items";
    private static final Gson GSON = new Gson();
    DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void accept(Event e) {
        e.ifType(NewItemEvent.class, this::saveIfNeeded)
                .ifType(UpdatedItemEvent.class, this::saveIfNeeded)
                .ifType(RetrievedItemEvent.class, this::saveIfNeeded)
                .ifType(DeletedItemEvent.class, this::moveToTrash);
    }

    private String backupPath() {
        return Main.settings.getMedia().getBackupPath();
    }

    private void moveToTrash(DeletedItemEvent event) throws IOException {
        moveMediaToTrash(event);
        moveMetadataToTrash(event);
    }

    private void moveMetadataToTrash(DeletedItemEvent event) throws IOException {
        Item item = event.getItem();
        Path fullFolderPath = getFullFolderPath(item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName() + ".json");
        Path fullTrashFolderPath = getTrashFolderPath(item);
        Path trashFilePath = Paths.get(fullTrashFolderPath.toString(), item.getFileName() + ".json");
        Files.move(filePath, trashFilePath);
    }

    private void moveMediaToTrash(DeletedItemEvent event) throws IOException {
        Item item = event.getItem();
        Path fullFolderPath = getFullFolderPath(item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Path fullTrashFolderPath = getTrashFolderPath(item);
        Path trashFilePath = Paths.get(fullTrashFolderPath.toString(), item.getFileName());
        Files.move(filePath, trashFilePath);
    }

    private Path getTrashFolderPath(Item item) throws IOException {
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path fullTrashFolderPath = Paths.get(backupPath(), DELETED, dateStr, item.getGoogleID());
        Files.createDirectories(fullTrashFolderPath);
        return fullTrashFolderPath;
    }

    private void saveIfNeeded(Event event) throws IOException {
        saveItemFile(event);
        saveMetadataFile(event);
    }

    private void saveMetadataFile(Event event) throws IOException {
        File metadataFile = getMetadataFile(event.getItem());
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

    private File getMetadataFile(Item item) throws IOException {
        Path fullFolderPath = getFullFolderPath(item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName() + ".json");
        Files.createDirectories(fullFolderPath);
        return new File(filePath.toAbsolutePath().toString());
    }

    private File getItemFile(Item item) throws IOException {
        Path fullFolderPath = getFullFolderPath(item);
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Files.createDirectories(fullFolderPath);
        return new File(filePath.toAbsolutePath().toString());
    }

    private Path getFullFolderPath(Item item) throws IOException {
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path path = Paths.get(backupPath(), ITEMS, dateStr, item.getGoogleID());
        Files.createDirectories(path);
        return path;
    }

    private void saveItemFile(Event event) throws IOException {
        Item item = event.getItem();
        File file = getItemFile(item);
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

    @Override
    public ImageWithMetadata getItem(Dimension size, Item item) {
        try {
            return getItemException(size, item);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return null;
    }

    public ImageWithMetadata getItemException(Dimension size, Item item) throws IOException {
        File file = getItemFile(item);
        if (!file.exists()) {
            return null;
        }
        Image image = ImageUtil.scaledImage(ImageIO.read(file), size);
        MediaMetadata metadata = null;
        try {
            File metadataFile = getMetadataFile(item);
            if (file.exists()) {
                metadata = GSON.fromJson(new FileReader(metadataFile), MediaMetadata.class);
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        if(metadata != null) {
            return new ImageWithMetadata(image, ImageWithMetadata.convert(metadata));
        } else {
            return new ImageWithMetadata(image);
        }
    }
}
