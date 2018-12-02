package org.sr3u.photoframe.server.data;

import com.google.photos.library.v1.proto.MediaItem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sr3u.photoframe.server.DateUtil;

import java.util.Date;

@Data
@EqualsAndHashCode
@ToString
@DatabaseTable(tableName = "MediaItems")
@NoArgsConstructor
public class Item {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String googleID;
    @DatabaseField
    long creationTimestamp;
    @DatabaseField
    long validUntil;


    public Item(MediaItem mediaItem) {
        this.googleID = mediaItem.getId();
        this.creationTimestamp = mediaItem.getMediaMetadata().getCreationTime().getSeconds();
        this.validUntil = defaultCleanupTimestamp();
    }

    public static long defaultCleanupTimestamp() {
        return DateUtil.timestamp(DateUtil.addDays(new Date(), 7));
    }


}
