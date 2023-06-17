package org.pipeman.ytdl;

import io.javalin.Javalin;
import org.pipeman.ytdl.utils.FileCache;

import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.get;

public class Main {
    private static final FileCache fileCache = new FileCache();
    public static boolean debug = false;

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--dev")) debug = true;
        Javalin app = Javalin.create(c -> c.showJavalinBanner = false).start(Config.get().serverPort);

        app.routes(() -> {
            get("", ctx -> ctx.html(fileCache.get(Config.get().indexPath)));

            get("download", Api::download);
            get("formats", Api::getFormats);
            get("search", Api::search);
        });

        /* start download: https://loader.to/ajax/download.php?format=wav&url=https://www.youtube.com/watch?v=4l9P4P_mxNk
         -> response: {"success": true, "id": "", "title": ""

         query progress: https://loader.to/ajax/progress.php?id=<id>
         -> response: {"success":0,"progress":0,"download_url":null,"text":null}
          // progress: 0-1000

          // not found: text: "Error"


          // formats: 1080, wav, mp3

*/
    }
}
