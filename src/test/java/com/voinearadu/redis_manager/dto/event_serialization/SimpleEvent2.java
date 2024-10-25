package com.voinearadu.redis_manager.dto.event_serialization;

import com.voinearadu.utils.redis_manager.event.RedisRequest;
import com.voinearadu.utils.redis_manager.manager.RedisManager;
import lombok.Getter;

import java.util.List;

@Getter
public class SimpleEvent2 extends RedisRequest<String> {

    private final List<String> a;
    private final String b;

    public SimpleEvent2(RedisManager eventManager, List<String> a, String b) {
        super(eventManager, eventManager.getRedisConfig().getRedisID());

        this.a = a;
        this.b = b;
    }

}
