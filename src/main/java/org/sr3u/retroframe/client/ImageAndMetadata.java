package org.sr3u.retroframe.client;

import lombok.Builder;
import lombok.Value;

import java.awt.image.BufferedImage;
import java.util.Map;

@Value
@Builder
public class ImageAndMetadata {
    int imageByteSize;
    int metadataByteSize;
    Map<String, Object> metaData;
    BufferedImage image;
}
