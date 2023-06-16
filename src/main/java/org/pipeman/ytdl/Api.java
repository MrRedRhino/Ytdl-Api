package org.pipeman.ytdl;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Header;
import org.pipeman.ytdl.utils.DownloadType;
import org.pipeman.ytdl.utils.StrippedVideoInfo;
import org.pipeman.ytdl.utils.Utils;
import org.pipeman.ytdl.utils.Youtube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;

public class Api {
    private static final Logger LOGGER = LoggerFactory.getLogger(Api.class);

    public static void getFormats(Context ctx) {
        String videoID = ctx.pathParam("id");
        if (videoID.length() != 11) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }

        LOGGER.info(Utils.getIp(ctx) + ": Fetching video data for " + videoID);

        StrippedVideoInfo info = Youtube.getVideoInfo(videoID);
        if (info == null) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }
        ctx.json(info.toJson());
    }

    public static void download(Context ctx) {
        String videoID = ctx.pathParam("id");

        if (videoID.length() != 11) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }

        DownloadType type = DownloadType.get(ctx.queryParam("type"));

        if (type == null) {
            Responses.INVALID_TYPE.apply(ctx);
            return;
        }

        StrippedVideoInfo video = Youtube.getVideoInfo(videoID);
        if (video == null) {
            Responses.VIDEO_DOES_NOT_EXIST.apply(ctx);
            return;
        }
        if (video.audioUrl == null) type = DownloadType.AV;

        LOGGER.info(Utils.getIp(ctx) + ": Starting video download for " + videoID);

        ctx.contentType(ContentType.APPLICATION_OCTET_STREAM);
        ctx.header(Header.CONTENT_DISPOSITION,
                "attachment; filename=" + (video.titleForDownload + '.' + type.fileExtension));
        try {
            URLConnection conn = new URL(video.getDownloadUrl(type)).openConnection();
            ctx.header(Header.CONTENT_LENGTH, String.valueOf(conn.getContentLength()));
            conn.getInputStream().transferTo(ctx.res().getOutputStream());
        } catch (Exception ignored) {
        }
    }
}
