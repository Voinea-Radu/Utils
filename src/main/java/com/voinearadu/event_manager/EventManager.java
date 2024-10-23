package com.voinearadu.event_manager;

import com.voinearadu.event_manager.annotation.EventHandler;
import com.voinearadu.event_manager.dto.IEvent;
import com.voinearadu.logger.Logger;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
public class EventManager {

    private final HashMap<Class<?>, Object> objects = new HashMap<>();
    private final HashMap<Class<?>, List<EventMethod>> methods = new HashMap<>();
    private ExternalRegistrar externalRegister = (object, method, eventClass) -> false;
    private ExternalUnregister externalUnregister = (method, eventClass) -> false;

    public interface ExternalRegistrar {
        boolean execute(Object object, Method method, Class<?> eventClass);
    }

    public interface ExternalUnregister {
        boolean execute(Method method, Class<?> eventClass);
    }

    @SuppressWarnings("unused")
    public void registerExternalRegistrar(ExternalRegistrar register, ExternalUnregister unregister) {
        externalRegister = register;
        externalUnregister = unregister;
    }

    public void register(Class<?> clazz) {
        try {
            register(clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException error) {
            Logger.error(error);
        }
    }

    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            register(object, method);
        }

        sortMethods();
    }

    @SuppressWarnings("unused")
    public void unregister(Object object) {
        for (Method method : object.getClass().getMethods()) {
            unregister(method);
        }
    }

    public void fire(Object event) {
        Class<?> eventClass = event.getClass();

        if (!methods.containsKey(eventClass)) {
            return;
        }

        List<EventMethod> eventMethods = methods.get(eventClass);

        for (EventMethod method : eventMethods) {
            method.fire(event);
        }
    }

    protected Class<?> getEventClass(Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) {
            return null;
        }

        EventHandler annotation = method.getAnnotation(EventHandler.class);

        if (annotation.ignore()) {
            return null;
        }

        if (method.getParameterCount() != 1) {
            Logger.error("Method " + method.getName() + " from class " + method.getDeclaringClass() + " has " + method.getParameterCount() + " parameters, expected 1");
            return null;
        }

        return method.getParameterTypes()[0];
    }

    private void register(Object object, Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalRegister.execute(object, method, eventClass);

            if (!result) {
                Logger.error("Failed to register method " + method.getName() + " from class " + method.getDeclaringClass() + " with event class " + eventClass.getName());
            }

            return;
        }

        Class<?> parentClass = object.getClass();

        if (!objects.containsKey(parentClass)) {
            objects.put(parentClass, object);
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        eventMethods.add(new EventMethod(method));
        methods.put(eventClass, eventMethods);
    }

    private void unregister(Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalUnregister.execute(method, eventClass);

            if (!result) {
                Logger.error("Failed to register method " + method.getName() + " from class " + method.getDeclaringClass() + " with event class " + eventClass.getName());
            }

            return;
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        eventMethods.remove(new EventMethod(method));
        methods.put(eventClass, eventMethods);
    }

    private void sortMethods() {
        for (Class<?> eventClass : methods.keySet()) {
            List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
            eventMethods.sort(new EventMethod.Comparator());
            methods.put(eventClass, eventMethods);
        }
    }

    public class EventMethod {
        private final Method method;
        private final EventHandler annotation;

        public EventMethod(Method method) {
            this.method = method;
            this.annotation = method.getAnnotation(EventHandler.class);
        }

        public void fire(Object event) {
            try {
                method.setAccessible(true);
                Object object = objects.get(method.getDeclaringClass());
                method.invoke(object, event);
            } catch (IllegalAccessException | InvocationTargetException error) {
                Logger.error(error);
            }
        }

        public static class Comparator implements java.util.Comparator<EventMethod> {
            @Override
            public int compare(EventMethod object1, EventMethod object2) {
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
}
