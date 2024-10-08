package com.voinearadu.redis_manager.event;

import com.voinearadu.redis_manager.manager.RedisManager;
import org.jetbrains.annotations.NotNull;

public class RedisEvent extends RedisRequest<Void> {

    public RedisEvent(@NotNull RedisManager redisManager, @NotNull String className, long id, @NotNull String originator, @NotNull String target) {
        super(redisManager, className, id, originator, target);
    }

    public RedisEvent(@NotNull RedisManager redisManager, @NotNull String target) {
        super(redisManager, target);
    }

    @Override
    public void fireEvent() {
        redisManager.getEventManager().fire(this);
        respond(null); // Mark the event as completed
    }

}
