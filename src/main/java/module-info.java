module com.voinearadu.utils {
    exports com.voinearadu.event_manager;
    exports com.voinearadu.file_manager;
    exports com.voinearadu.lambda;
    exports com.voinearadu.lambda.lambda;
    exports com.voinearadu.logger;
    exports com.voinearadu.logger.dto;
    exports com.voinearadu.message_builder;
    exports com.voinearadu.redis_manager;
    exports com.voinearadu.reflections;
    exports com.voinearadu.generic;

    requires static lombok;
    requires static org.jetbrains.annotations;
    requires static com.google.gson;
    requires static org.apache.commons.io;
    requires static org.slf4j;
    requires static redis.clients.jedis;
    requires static org.apache.commons.pool2;
}