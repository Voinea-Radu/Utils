package com.voinearadu.utils.redis_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.voinearadu.utils.event_manager.EventManager;
import com.voinearadu.utils.file_manager.FileManager;
import com.voinearadu.utils.message_builder.MessageBuilderManager;
import com.voinearadu.utils.redis_manager.dto.RedisConfig;
import com.voinearadu.utils.redis_manager.manager.DebugRedisManager;

import java.util.List;

public class Main {

    public Main() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        FileManager fileManager = new FileManager(() -> gson, "config");
        MessageBuilderManager.init(true);

        RedisConfig config = fileManager.load(RedisConfig.class, "");

        new DebugRedisManager(() -> gson, config, getClass().getClassLoader(),
                new EventManager(), true, false, List.of(
                "kingdoms_core_dev#*"
        )
        );
    }

}
