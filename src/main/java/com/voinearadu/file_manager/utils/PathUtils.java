package com.voinearadu.file_manager.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public class PathUtils {

    private final static Pattern snakeCasePattern = Pattern.compile("([A-Z])");

    public static String join(String... paths) {
        StringBuilder builder = new StringBuilder();

        for (String path : paths) {
            builder.append(path);
            builder.append('/');
        }

        return builder.toString();
    }

    @SuppressWarnings("unused")
    public static String join(List<String> paths) {
        return join(paths.toArray(new String[0]));
    }

    public static @NotNull String toSnakeCase(@NotNull String string) {
        String output = snakeCasePattern.matcher(string).replaceAll("_$1").toLowerCase();

        if (output.startsWith("_")) {
            output = output.substring(1);
        }

        return output;
    }

}
