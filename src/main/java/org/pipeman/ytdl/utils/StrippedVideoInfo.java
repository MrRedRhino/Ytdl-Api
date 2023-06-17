package org.pipeman.ytdl.utils;

import java.util.Map;

public class StrippedVideoInfo {
    private final long creation;
    private final String downloadId;
    private final Map<String, ?> jsonObject;
    private final String downloadName;

    public StrippedVideoInfo(String title, String downloadId, String formatName) {
        this.creation = System.currentTimeMillis();
        this.downloadId = downloadId;
        this.jsonObject = Map.of("title", title);

        this.downloadName = title.toLowerCase()
                                    .replaceAll("\\W+", "-")
                                    .replaceAll("^-+", "")
                                    .replaceAll("-+$", "") + '.' + formatName;
    }

    public Map<String, ?> toJson() {
        return jsonObject;
    }

    public long creation() {
        return creation;
    }

    public String downloadId() {
        return downloadId;
    }

    public String downloadName() {
        return downloadName;
    }
}
