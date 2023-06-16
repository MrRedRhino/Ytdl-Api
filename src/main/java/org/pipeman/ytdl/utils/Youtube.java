package org.pipeman.ytdl.utils;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import org.pipeman.ytdl.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Youtube {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final HashMap<String, StrippedVideoInfo> videoCache = new HashMap<>();
    private static final Map<String, Object> locks = new ConcurrentHashMap<>();

    private static StrippedVideoInfo fetchVideoResult(String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://loader.to/ajax/download.php?format=wav&url=" + id))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());


        YoutubeDownloader downloader = new YoutubeDownloader();

        RequestVideoInfo vRequest = new RequestVideoInfo(id);
        Response<VideoInfo> vResponse = downloader.getVideoInfo(vRequest);

        return vResponse.data() != null ? new StrippedVideoInfo(vResponse.data()) : null;

    }

    public static StrippedVideoInfo getVideoInfo(String id) {
        synchronized (locks.computeIfAbsent(id, k -> new Object())) {
            StrippedVideoInfo value = videoCache.get(id);

            if (value == null || value.expire() - Config.get().videoCacheDuration < System.currentTimeMillis()) {
                // Cache doesn't contain that id, or it expired, so we have to re-fetch it
                value = fetchVideoResult(id);
                if (value != null) videoCache.put(id, value);
            }
            locks.remove(id);
            return value;
        }
    }
}
