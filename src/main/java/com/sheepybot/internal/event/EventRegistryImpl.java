package com.sheepybot.internal.event;

import com.google.common.collect.Maps;
import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.api.entities.event.*;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.exception.event.EventException;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class EventRegistryImpl implements RootEventRegistry {

    private final Map<Class<? extends GenericEvent>, List<RegisteredListener>> listeners;

    public EventRegistryImpl() {
        this.listeners = Maps.newConcurrentMap();
    }

    @Override
    public void callEvent(@NotNull(value = "event cannot be null") final Event event) {
        this.fireEvent(event);
    }

    @Override
    public void registerEvents(@NotNull(value = "listener cannot be null") final EventListener listener,
                               @NotNull(value = "module cannot be null") final Module module) {

        Arrays.stream(listener.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(EventHandler.class)
                && !method.isBridge() && !method.isSynthetic()).forEach(method -> {

            final EventHandler handler = method.getAnnotation(EventHandler.class);

            final Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length == 1 && !Event.class.isAssignableFrom(parameters[0])) {

                final Class<? extends GenericEvent> eventClass = parameters[0].asSubclass(GenericEvent.class);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                final List<RegisteredListener> registrations = this.listeners.computeIfAbsent(eventClass, k -> new ArrayList<>());

                final EventExecutor executor = (eventListener, event) -> {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(eventListener, event);
                    } catch (final IllegalAccessException | InvocationTargetException ex) {
                        throw new EventException(ex);
                    }
                };

                registrations.add(new RegisteredListener(listener, executor, handler, module));

                this.listeners.put(eventClass, registrations);

            }

        });

    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "event cannot be null") final Event event) {
        return this.listeners.keySet().stream().filter(clazz -> clazz.isAssignableFrom(event.getClass()))
                .flatMap(clazz -> this.listeners.get(clazz).stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "module cannot be null") final Module module) {
        return this.listeners.values().stream().flatMap(listeners -> listeners.stream().filter(listener ->
                listener.getModule() == module)).collect(Collectors.toList());
    }

    @Override
    public void unregisterAll(@NotNull(value = "module cannot be null") final Module module) {
        this.listeners.keySet().forEach(clazz -> this.listeners.get(clazz).removeIf(listener -> listener.getModule() == module));
    }

    @Override
    public void unregisterAll() {
        this.listeners.clear();
    }

    private void fireEvent(@NotNull(value = "event cannot be null") final Event event) {
        this.getRegisteredListeners(event).forEach(listener -> {
            try {
                listener.callEvent(event);
            } catch (final Throwable ex) {
                LOGGER.info("Could not pass event " + event.getClass().getName() + " to " + listener.getModule().getFullName(), ex);
            }
        });
    }
}
