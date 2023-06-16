package org.pipeman.ytdl.utils;

import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;

import java.util.HashMap;
import java.util.Map;

import static org.pipeman.ytdl.Main.debug;

public class StrippedVideoInfo {
    public final String videoAndAudioUrl;
    public final String titleForDownload;
    public final String audioUrl;
    private final long expire;
    private final Map<String, ?> jsonObject;

    public StrippedVideoInfo(VideoInfo v) {
        String thumbnailURL = "";

        for (String img : v.details().thumbnails()) {
            if (img.contains("hqdefault")) {
                thumbnailURL = img;
                break;
            }
        }

        String id = v.details().videoId();
        this.expire = System.currentTimeMillis() + 3_600_000 * 4;

        this.videoAndAudioUrl = v.bestVideoWithAudioFormat().url();

        Map<String, String> formats = new HashMap<>();
        formats.put("av", downloadURL(id, "av"));

        AudioFormat audioFormat = v.bestAudioFormat();
        if (audioFormat != null) {
            this.audioUrl = audioFormat.url();
            formats.put("audio", downloadURL(id, "audio"));
        } else {
            this.audioUrl = null;
        }

        this.jsonObject = Map.of(
                "title", v.details().title(),
                "author", v.details().author(),
                "thumbnail", thumbnailURL,
                "duration", v.details().lengthSeconds(),
                "downloads", formats
        );

        this.titleForDownload = v.details().title().toLowerCase().replaceAll("\\W+", "-");
    }

    private static String downloadURL(String id, String type) {
        return debug ? "/download/" + id + "?type=" + type : "https://ytdl.pipeman.org/download/" + id + "?type=" + type;
    }

    public String getDownloadUrl(DownloadType type) {
        return type == DownloadType.AV ? videoAndAudioUrl : audioUrl;
    }

    public Map<String, ?> toJson() {
        return jsonObject;
    }

    public long expire() {
        return expire;
    }
}
