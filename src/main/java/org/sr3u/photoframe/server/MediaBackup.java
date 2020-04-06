package org.sr3u.photoframe.server;

import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaMetadata;
import org.sr3u.photoframe.server.data.Item;
import org.sr3u.photoframe.server.data.MediaType;
import org.sr3u.photoframe.server.events.DeletedItemEvent;
import org.sr3u.photoframe.server.events.Event;
import org.sr3u.photoframe.server.events.NewItemEvent;
import org.sr3u.photoframe.server.events.UpdatedItemEvent;
import sr3u.streamz.functionals.Consumerex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaBackup implements Consumerex<Event> {
    public static final String DELETED = "deleted";
    private static final String ITEMS = "items";
    DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void accept(Event e) throws Exception {
        String backupPath = Server.settings.getMedia().getBackupPath();
        e.ifType(NewItemEvent.class, event -> saveIfNeeded(backupPath, event))
                .ifType(UpdatedItemEvent.class, event -> saveIfNeeded(backupPath, event))
                .ifType(DeletedItemEvent.class, event -> {
                    moveToTrash(backupPath, event);
                });
    }

    private void moveToTrash(String backupPath, DeletedItemEvent event) throws IOException {
        Item item = event.getItem();
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path fullFolderPath = Paths.get(backupPath, ITEMS, dateStr, item.getGoogleID());
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Files.createDirectories(fullFolderPath);
        Path fullTrashFolderPath = Paths.get(backupPath, DELETED, dateStr, item.getGoogleID());
        Files.createDirectories(fullTrashFolderPath);
        Path trashFilePath = Paths.get(fullTrashFolderPath.toString(), item.getFileName());
        Files.move(filePath, trashFilePath);
    }

    private void saveIfNeeded(String backupPath, Event event) throws IOException {
        Item item = event.getItem();
        String dateStr = YYYY_MM_DD.format(new Date(item.getCreationTimestamp() * 1000));
        Path fullFolderPath = Paths.get(backupPath, ITEMS, dateStr, item.getGoogleID());
        Path filePath = Paths.get(fullFolderPath.toAbsolutePath().toString(), item.getFileName());
        Files.createDirectories(fullFolderPath);
        File file = new File(filePath.toAbsolutePath().toString());
        if (!file.exists()) {
            MediaItem mediaItem = event.getMediaItem();
            MediaMetadata mediaMetadata = mediaItem.getMediaMetadata();
            String itemUrlString = mediaItem.getBaseUrl();
            if (item.getMediaType() == MediaType.IMAGE) {
                itemUrlString += ImageUtil.googlePhotoSize(mediaMetadata.getWidth(), mediaMetadata.getHeight());
            } else if (item.getMediaType() == MediaType.VIDEO) {
                itemUrlString += "=dv";
            }
            URL itemUrl = new URL(itemUrlString);
            ReadableByteChannel rbc = Channels.newChannel(itemUrl.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Saved item " + file.getAbsolutePath());
        }
    }
}
