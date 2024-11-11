package com.voinearadu.utils.event_manager.dto;

import com.voinearadu.utils.event_manager.annotation.EventHandler;
import com.voinearadu.utils.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class EventMethod {
    private final Method method;
    private final Object parentObject;
    private final EventHandler annotation;

    public EventMethod(Object parentObject, @NotNull Method method) {
        this.parentObject = parentObject;
        this.method = method;
        this.annotation = method.getAnnotation(EventHandler.class);
    }

    public void fire(Object event) {
        try {
            method.setAccessible(true);
            method.invoke(parentObject, event);
        } catch (IllegalAccessException | InvocationTargetException error) {
            Logger.error(error);
        }
    }

    public static class Comparator implements java.util.Comparator<EventMethod> {
        @Override
        public int compare(@NotNull EventMethod object1, @NotNull EventMethod object2) {
            return object1.annotation.order() - object2.annotation.order();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EventMethod eventMethod)) {
            return false;
        }

        return eventMethod.method.equals(method);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }
}