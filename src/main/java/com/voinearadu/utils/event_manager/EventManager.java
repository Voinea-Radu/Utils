package com.voinearadu.utils.event_manager;

import com.voinearadu.utils.event_manager.annotation.EventHandler;
import com.voinearadu.utils.event_manager.dto.EventMethod;
import com.voinearadu.utils.event_manager.dto.IEvent;
import com.voinearadu.utils.logger.Logger;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
public class EventManager {

    private final HashMap<Class<?>, List<EventMethod>> methods = new HashMap<>();
    private ExternalRegistrar externalRegistrar = new ExternalRegistrar() {
        @Override
        public boolean register(Object object, Method method, Class<?> eventClass) {
            return false;
        }

        @Override
        public boolean unregister(Method method, Class<?> eventClass) {
            return false;
        }
    };

    /**
     * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
     */
    public interface ExternalRegistrar {
        /**
         * @param object     The parent object where the event method is located
         * @param method     The actual event method
         * @param eventClass The event class
         * @return true if the method was registered successfully, false otherwise
         */
        boolean register(Object object, Method method, Class<?> eventClass);

        /**
         * @param method     The actual event method
         * @param eventClass The event class
         * @return true if the method was unregistered successfully, false otherwise
         */
        boolean unregister(Method method, Class<?> eventClass);
    }

    /**
     * Used to register an external registrar, that can catch any event that does not implement {@link IEvent}
     *
     * @param externalRegistrar the external registrar
     */
    public void registerExternalRegistrar(ExternalRegistrar externalRegistrar) {
        this.externalRegistrar = externalRegistrar;
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

    private void register(Object parentObject, Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalRegistrar.register(parentObject, method, eventClass);

            if (!result) {
                Logger.error("Failed to register method " + method.getName() + " from class " + method.getDeclaringClass() + " with event class " + eventClass.getName());
            }

            return;
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        eventMethods.add(new EventMethod(parentObject, method));
        methods.put(eventClass, eventMethods);
    }

    private void unregister(Method method) {
        Class<?> eventClass = getEventClass(method);

        if (eventClass == null) {
            return;
        }

        if (!IEvent.class.isAssignableFrom(eventClass)) {
            boolean result = externalRegistrar.unregister(method, eventClass);

            if (!result) {
                Logger.error("Failed to register method " + method.getName() + " from class " + method.getDeclaringClass() + " with event class " + eventClass.getName());
            }

            return;
        }

        List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
        eventMethods.removeIf(eventMethod -> eventMethod.getMethod().equals(method));
        methods.put(eventClass, eventMethods);
    }

    private void sortMethods() {
        for (Class<?> eventClass : methods.keySet()) {
            List<EventMethod> eventMethods = methods.getOrDefault(eventClass, new ArrayList<>());
            eventMethods.sort(new EventMethod.Comparator());
            methods.put(eventClass, eventMethods);
        }
    }


}
