package com.sheepybot.api.entities.event;

import com.sheepybot.api.entities.module.Module;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public interface RootEventRegistry {

    Logger LOGGER = LoggerFactory.getLogger(RootEventRegistry.class);

    /**
     * @param event The event to call
     */
    void callEvent(@NotNull("event cannot be null") final GenericEvent event);

    /**
     * Registers all {@link Event}s in the given {@link RootEventRegistry} class
     *
     * @param listener The {@link EventListener} to register
     * @param module   The {@link Module} to register
     */
    void registerEvents(@NotNull("listener cannot be null") final EventListener listener,
                        @NotNull("parent module cannot be null") final Module module);

    /**
     * @param event The event
     * @return A {@link Collection} containing every {@link RegisteredListener} registered to this {@link Event}
     */
    Collection<RegisteredListener> getRegisteredListeners(@NotNull("event cannot be null") final GenericEvent event);

    /**
     * @param module The {@link Module} to get the listeners of
     * @return A {@link Collection} containing every listener registered to the {@link Module}
     */
    Collection<RegisteredListener> getRegisteredListeners(@NotNull("parent module cannot be null") final Module module);

    /**
     * Unregisters every listener for the {@link Module}
     *
     * @param module The {@link Module} to unregister
     */
    void unregisterAll(@NotNull("parent module cannot be null") final Module module);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();
}
