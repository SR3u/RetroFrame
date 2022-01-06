package org.sr3u.retroframe.server;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Resources {
    private Resources() {
    }

    public static final String TITLE = "Photos Library API Sample";
    public static final String GOOGLE_PHOTOS_ICON_RESOURCE = "google_photos.png";

    public static final List<String> REQUIRED_SCOPES = ImmutableList.of("https://www.googleapis.com/auth/photoslibrary.readonly");
    public static final String BACK_ICON_RESOURCE = "/assets/back.png";
    public static final String ALBUM_ICON_RESOURCE = "/assets/album.png";
}
