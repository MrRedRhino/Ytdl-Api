package org.pipeman.ytdl;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Header;
import io.sfrei.tracksearch.exceptions.TrackSearchException;
import org.json.JSONObject;
import org.pipeman.ytdl.utils.StrippedVideoInfo;
import org.pipeman.ytdl.utils.Utils;
import org.pipeman.ytdl.utils.Youtube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(Api.class);

    public static void getFormats(Context ctx) {
        String videoID = ctx.queryParam("id");
        String format = ctx.queryParam("format");

        LOGGER.info(Utils.getIp(ctx) + ": Fetching video data for " + videoID);

        StrippedVideoInfo info = Youtube.getVideoInfo(videoID, format);
        if (info == null) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }
        ctx.json(info.toJson());
    }

    public static void search(Context ctx) throws TrackSearchException {
        String query = ctx.queryParam("query");
        if (query == null) {
            Responses.QUERY_INVALID.apply(ctx);
            return;
        }
        String limitString = ctx.queryParam("limit");
        int limit = Utils.optTry(() -> Integer.parseInt(limitString == null ? "" : limitString)).orElse(10);

        List<Map<String, ?>> json = new ArrayList<>();
        for (Youtube.Result result : Youtube.searchYoutube(query, limit)) {
            json.add(result.toJson());
        }
        ctx.json(json);
    }

    public static void download(Context ctx) throws InterruptedException, IOException {
        String videoID = ctx.queryParam("id");
        String format = ctx.queryParam("format");

        StrippedVideoInfo video = Youtube.getVideoInfo(videoID, format);
        if (video == null) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }

        LOGGER.info(Utils.getIp(ctx) + ": Starting video download for " + videoID);

        while (true) {
            JSONObject object = fetchProgress(video);
            if (object.optString("text", "").equalsIgnoreCase("error")) {
                return;
            }

            if (!object.optString("download_url", "").isEmpty()) {
                download(ctx, video, object);
                return;
            }

            Thread.sleep(1000);
        }
    }

    private static JSONObject fetchProgress(StrippedVideoInfo video) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://loader.to/ajax/progress.php?id=" + video.downloadId()))
                .header("Accept-Encoding", "text")
                .build();

        HttpResponse<String> response = Youtube.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    private static void download(Context ctx, StrippedVideoInfo video, JSONObject object) throws IOException {
        ctx.contentType(ContentType.APPLICATION_OCTET_STREAM);
        ctx.header(Header.CONTENT_DISPOSITION, "attachment; filename=" + (video.downloadName()));
        URLConnection conn = new URL(object.getString("download_url")).openConnection();
        ctx.header(Header.CONTENT_LENGTH, String.valueOf(conn.getContentLength()));
        conn.getInputStream().transferTo(ctx.res().getOutputStream());
    }
}
