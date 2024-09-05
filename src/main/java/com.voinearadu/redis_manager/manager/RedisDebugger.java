package com.voinearadu.redis_manager.manager;

import com.voinearadu.logger.Logger;
import com.voinearadu.message_builder.MessageBuilder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RedisDebugger {

    private static final MessageBuilder creatingListener = new MessageBuilder("Creating RedisManager with listenID: %id%");
    private static final MessageBuilder receiveResponse = new MessageBuilder("[Receive-Response   ] [%channel%] %response%");
    private static final MessageBuilder receive = new MessageBuilder("[Receive            ] [%channel%] %event%");
    private static final MessageBuilder subscribed = new MessageBuilder("Subscribed to channel %channel%");
    private static final MessageBuilder unsubscribed = new MessageBuilder("Unsubscribed to channel %channel%");
    private static final MessageBuilder sendResponse = new MessageBuilder("[Send-Response      ] [%channel%] %response%");
    private static final MessageBuilder send = new MessageBuilder("[Send               ] [%channel%] %event%");
    private static final MessageBuilder registeringMethod = new MessageBuilder("Registering method %method% from class %class%");
    private boolean enabled;

    public RedisDebugger(boolean enabled) {
        this.enabled = enabled;
    }

    @SuppressWarnings("unused")
    public void enable() {
        enabled = true;
    }

    @SuppressWarnings("unused")
    public void disable() {
        enabled = false;
    }

    public void creatingListener(String id) {
        print(creatingListener
                .parse("id", id)
                .parse());
    }

    public void receiveResponse(String channel, String response) {
        print(receiveResponse
                .parse("channel", channel)
                .parse("response", response)
                .parse());
    }

    public void receive(String channel, String event) {
        print(receive
                .parse("channel", channel)
                .parse("event", event)
                .parse());
    }

    public void subscribed(String channel) {
        print(subscribed
                .parse("channel", channel)
                .parse());
    }

    public void unsubscribed(String channel) {
        print(unsubscribed
                .parse("channel", channel)
                .parse());
    }

    public void sendResponse(String channel, String response) {
        print(sendResponse
                .parse("channel", channel)
                .parse("response", response)
                .parse());
    }

    public void send(String channel, String event) {
        print(send
                .parse("channel", channel)
                .parse("event", event)
                .parse());
    }

    @SuppressWarnings("unused")
    public void registeringMethod(String method, String clazz) {
        print(registeringMethod
                .parse("method", method)
                .parse("class", clazz)
                .parse());
    }

    private void print(String message) {
        if (!enabled) {
            return;
        }
        Logger.log(message);
    }

}
