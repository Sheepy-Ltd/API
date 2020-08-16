package com.sheepybot.internal.event;

import com.google.common.collect.Maps;
import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.api.entities.event.*;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.exception.event.EventException;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class EventRegistryImpl implements RootEventRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRegistryImpl.class);

    private final Map<Class<? extends GenericEvent>, List<RegisteredListener>> listeners;

    public EventRegistryImpl() {
        this.listeners = Maps.newConcurrentMap();
    }

    @Override
    public void callEvent(@NotNull(value = "event cannot be null") final GenericEvent event) {
        this.fireEvent(event);
    }

    @Override
    public void registerEvents(@NotNull(value = "listener cannot be null") final EventListener listener,
                               @NotNull(value = "module cannot be null") final Module module) {

        LOGGER.debug("Registering listeners from class {}.{} in module {}...", listener.getClass().getPackage().getName(), listener.getClass().getName(), module.getName());

        Arrays.stream(listener.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(EventHandler.class)
                && !method.isBridge() && !method.isSynthetic()).forEach(method -> {

            final EventHandler handler = method.getAnnotation(EventHandler.class);

            final Class<?>[] parameters = method.getParameterTypes();
            if (handler != null && parameters.length == 1 && GenericEvent.class.isAssignableFrom(parameters[0])) {

                LOGGER.debug("Found valid event handler method {}({})...", method.getName(), parameters[0].getName());

                final Class<? extends GenericEvent> eventClass = parameters[0].asSubclass(GenericEvent.class);

                if (!method.isAccessible()) {
                    LOGGER.debug("Method is private, changing method access to public...");
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

                LOGGER.debug("Creating new registered listener from event class...");

                registrations.add(new RegisteredListener(listener, executor, handler, module));

                LOGGER.debug("Registering registered listener in event registry...");

                this.listeners.put(eventClass, registrations);

            }

        });

    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "event cannot be null") final GenericEvent event) {
        LOGGER.debug("Retrieving registered listeners for event {}...", event.getClass().getName());
        return this.listeners.keySet().stream().filter(clazz -> clazz.isAssignableFrom(event.getClass()))
                .flatMap(clazz -> this.listeners.get(clazz).stream()).collect(Collectors.toList());
    }

    @Override
    public Collection<RegisteredListener> getRegisteredListeners(@NotNull(value = "module cannot be null") final Module module) {
        LOGGER.debug("Retrieving registered listeners of module {}...", module.getName());
        return this.listeners.values().stream().flatMap(listeners -> listeners.stream().filter(listener ->
                listener.getModule() == module)).collect(Collectors.toList());
    }

    @Override
    public void unregisterAll(@NotNull(value = "module cannot be null") final Module module) {
        LOGGER.debug("Unregistering event listeners of module {}...", module.getName());
        this.listeners.keySet().forEach(clazz -> this.listeners.get(clazz).removeIf(listener -> listener.getModule() == module));
    }

    @Override
    public void unregisterAll() {
        LOGGER.debug("Unregistering all event listeners...");
        this.listeners.clear();
    }

    private void fireEvent(@NotNull(value = "event cannot be null") final GenericEvent event) {
        LOGGER.debug("Retrieved fireEvent call for event {}...", event.getClass().getName());
        this.getRegisteredListeners(event).forEach(listener -> {
            LOGGER.debug("Passing event {} to registered listener class {} in module {}", event.getClass().getName(), listener.getListener().getClass().getName(), listener.getModule().getName());
            try {
                listener.callEvent(event);
            } catch (final Throwable ex) {
                LOGGER.error("Could not pass event " + event.getClass().getName() + " to " + listener.getModule().getFullName(), ex);
            }
        });
    }
}
