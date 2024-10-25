package com.voinearadu.utils.logger.dto;

import org.jetbrains.annotations.NotNull;


public enum ConsoleColor {
    RESET("\033[0m"),
    BLACK("\033[0;30m"),
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    YELLOW("\033[0;33m"),
    BLUE("\033[0;34m"),
    PURPLE("\033[0;35m"),
    CYAN("\033[0;36m"),
    WHITE("\033[0;37m");

    private final @NotNull String code;

    ConsoleColor(@NotNull String code) {
        this.code = code;
    }

    @SuppressWarnings("unused")
    public static @NotNull String clearString(@NotNull String log) {
        for (ConsoleColor value : ConsoleColor.values()) {
            log = log.replace(value.toString(), "");
        }
        return log;
    }

    @Override
    public @NotNull String toString() {
        return code;
    }
}