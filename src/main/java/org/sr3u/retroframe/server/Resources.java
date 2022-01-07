package org.sr3u.retroframe.server;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Resources {
    private Resources() {
    }

    public static final List<String> REQUIRED_SCOPES = ImmutableList.of("https://www.googleapis.com/auth/photoslibrary.readonly");
}
