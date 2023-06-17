package org.pipeman.ytdl;

import io.javalin.http.Context;

import java.util.Map;

public enum Responses {
    VIDEO_DOES_NOT_EXIST(1, 400, "Video does not exist on YouTube"),
    QUERY_INVALID(2, 400, "Query is empty");

    private final int id;
    private final String name;
    private final String desc;
    private final int status;

    Responses(int id, int status, String desc) {
        this.id = id;
        this.name = name().toLowerCase().replace('_', '-');
        this.desc = desc;
        this.status = status;
    }

    public void apply(Context ctx) {
        ctx.status(status).json(Map.of(
                "error", Map.of(
                        "id", id,
                        "name", name,
                        "description", desc
                )
        ));
    }
}
