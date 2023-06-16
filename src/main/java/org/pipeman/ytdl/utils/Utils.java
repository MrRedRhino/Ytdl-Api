package org.pipeman.ytdl.utils;

import io.javalin.http.Context;

import java.util.Optional;

public class Utils {
    public static <T> Optional<T> optTry(ThrowingSupplier<T> action) {
        try {
            return Optional.ofNullable(action.run());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static String getIp(Context ctx) {
        return Optional.ofNullable(ctx.header("X-Real-IP")).orElse(ctx.ip());
    }

    public interface ThrowingSupplier<T> {
        T run() throws Exception;
    }
}
