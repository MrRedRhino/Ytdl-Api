package org.pipeman.ytdl.utils;

public enum DownloadType {
    AV("mp4"),
    AUDIO("mp3");

    public final String fileExtension;

    DownloadType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static DownloadType get(String s) {
        return s == null ? AV : switch (s.toLowerCase()) {
            case "av" -> AV;
            case "audio" -> AUDIO;
            default -> null;
        };
    }
}
