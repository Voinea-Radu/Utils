package com.voinearadu.utils.redis_manager.event.impl;

import com.voinearadu.utils.redis_manager.event.RedisRequest;
import com.voinearadu.utils.redis_manager.manager.RedisManager;


@SuppressWarnings("unused")
public class PingEvent extends RedisRequest<Boolean> {

    public PingEvent(RedisManager redisManager, String target) {
        super(redisManager, target);
    }
}
