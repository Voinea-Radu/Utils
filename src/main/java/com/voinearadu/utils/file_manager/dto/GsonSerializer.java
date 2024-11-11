package com.voinearadu.utils.file_manager.dto;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class GsonSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    protected final ClassLoader classLoader;
    private final @Getter Class<T> serializedClass;

    protected GsonSerializer(ClassLoader classLoader, Class<T> serializedClass) {
        this.serializedClass = serializedClass;
        this.classLoader = classLoader;
    }

    public void register(@NotNull GsonBuilder builder) {
        builder.registerTypeAdapter(this.serializedClass, this);
    }
}
