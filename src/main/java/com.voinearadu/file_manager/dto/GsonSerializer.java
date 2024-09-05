package com.voinearadu.file_manager.dto;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import lombok.Getter;

public abstract class GsonSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    protected final ClassLoader classLoader;
    private final @Getter Class<T> serializedClass;

    protected GsonSerializer(ClassLoader classLoader, Class<T> serializedClass) {
        this.serializedClass = serializedClass;
        this.classLoader = classLoader;
    }
}
