package org.pipeman.ytdl;

import org.pipeman.pconf.AbstractConfig;
import org.pipeman.pconf.ConfigProvider;

import java.nio.file.Path;

public class Config extends AbstractConfig {
    public static final ConfigProvider<Config> CONF_PROVIDER = ConfigProvider.of("config.properties", Config::new);

    public final String indexPath = this.get("index-html-file", "deploy/index.html");
    public final int videoCacheDuration = this.get("video-cache-duration", 3_600_000);
    public final int serverPort = this.get("server-port", 4567);

    public Config(String file) {
        super(file);
        store(Path.of(file), "");
    }

    public static Config get() {
        return CONF_PROVIDER.c();
    }
}
