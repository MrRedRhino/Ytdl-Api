package org.pipeman.ytdl.utils;

import io.sfrei.tracksearch.clients.TrackSearchClient;
import io.sfrei.tracksearch.clients.youtube.YouTubeClient;
import io.sfrei.tracksearch.exceptions.TrackSearchException;
import io.sfrei.tracksearch.tracks.YouTubeTrack;
import org.json.JSONObject;
import org.pipeman.ytdl.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Youtube {
    public static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final HashMap<String, StrippedVideoInfo> videoCache = new HashMap<>();
    private static final Map<String, Object> locks = new ConcurrentHashMap<>();
    private static final TrackSearchClient<YouTubeTrack> SEARCH_CLIENT = new YouTubeClient();

    public static List<Youtube.Result> searchYoutube(String query, int limit) throws TrackSearchException {
        List<Youtube.Result> result = new ArrayList<>();

        for (YouTubeTrack track : SEARCH_CLIENT.getTracksForSearch(query)) {
            if (result.size() == limit) break;
            result.add(new Youtube.Result(track));
        }

        return result;
    }

    private static StrippedVideoInfo fetchVideoResult(String id, String format) {
        String url = "https://loader.to/ajax/download.php?format=%s&url=%s".formatted(format, id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject object = new JSONObject(response.body());
            String title = object.getString("title");
            String downloadID = object.getString("id");

            return new StrippedVideoInfo(title, downloadID, format);
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public static StrippedVideoInfo getVideoInfo(String id, String format) {
        String key = new JSONObject().put("id", id).put("format", format).toString();
        synchronized (locks.computeIfAbsent(key, k -> new Object())) {
            StrippedVideoInfo value = videoCache.get(key);

            if (value == null || value.creation() < System.currentTimeMillis() - Config.get().videoCacheDuration) {
                // Cache doesn't contain that key, or it expired, so we have to re-fetch it
                value = fetchVideoResult(id, format);
                if (value != null) videoCache.put(key, value);
            }
            locks.remove(key);
            return value;
        }
    }

    public record Result(String title, String url) {
        public Map<String, String> toJson() {
            return Map.of(
                    "url", url,
                    "title", title
            );
        }

        public Result(YouTubeTrack track) {
            this(track.getTitle(), track.getUrl());
        }
    }
}
