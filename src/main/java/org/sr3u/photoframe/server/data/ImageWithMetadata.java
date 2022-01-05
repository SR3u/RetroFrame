package org.sr3u.photoframe.server.data;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.photos.types.proto.MediaMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sr3u.photoframe.misc.util.ImageUtil;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public class ImageWithMetadata {

    private static final Logger log = LogManager.getLogger(ImageWithMetadata.class);

    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(new ImageWithMetadataFieldNamingStrategy())
            .create();
    private Image image;
    private Map<String, Object> metadata;

    public ImageWithMetadata(Image image) {
        this(image, new HashMap<>());
    }

    public String jsonMetadata() {
        return GSON.toJson(metadata);
    }

    public static Map<String, Object> convert(MediaMetadata mediaMetadata) {
        Map<String, Object> metadata = new HashMap<>();
        forEachField(mediaMetadata, metadata::put);
        return metadata;
    }


    private static void forEachField(Object obj, BiConsumer<String, Object> consumer) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object v = f.get(obj);
                if (v != null && !Modifier.isStatic(f.getModifiers())) {
                    consumer.accept(f.getName(), v);
                }
            } catch (Throwable e) {
                log.error(e);
                e.printStackTrace();
            }
        }
    }

    static class ImageWithMetadataFieldNamingStrategy implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            String fieldName = field.getName();
            if (fieldName.endsWith("_") && fieldName.length() > 1) {
                fieldName = fieldName.substring(0, fieldName.length() - 1);
            }
            return fieldName;
        }
    }

    public boolean tall() {
        return ImageUtil.isTall(image);
    }

    public boolean wide() {
        return ImageUtil.isWide(image);
    }

    public boolean square() {
        return ImageUtil.isSquare(image);
    }
}
