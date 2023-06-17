package org.pipeman.ytdl.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileCache {
    private final Map<String, String> cache = new HashMap<>();
    private final boolean useCache = false;

    public String get(String filename) {
        String content = cache.get(filename);
        if (content == null || !useCache) {
            try {
                content = Files.readString(Path.of(filename));
                cache.put(filename, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return content;
    }
}
