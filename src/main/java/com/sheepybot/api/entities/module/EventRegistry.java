package com.sheepybot.api.entities.module;

import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.event.Event;
import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.api.entities.event.RegisteredListener;
import com.sheepybot.api.entities.event.RootEventRegistry;

import java.util.Collection;

public final class EventRegistry {

    private final RootEventRegistry eventManager;
    private final Module module;

    EventRegistry(@NotNull(value = "eventManager cannot be null") final RootEventRegistry eventManager,
                  @NotNull(value = "module cannot be null") final Module module) {
        this.eventManager = eventManager;
        this.module = module;
    }

    /**
     * @return A {@link Collection} containing every {@link RegisteredListener} associated with the parent {@link
     * Module}
     */
    public Collection<RegisteredListener> getRegisteredListeners() {
        return this.eventManager.getRegisteredListeners(this.module);
    }

    /**
     * @param event The event to call
     */
    public void callEvent(@NotNull(value = "event cannot be null") final Event event) {
        this.eventManager.callEvent(event);
    }

    /**
     * Registers all {@link Event}s in the given {@link EventListener} class
     *
     * @param listener The {@link EventListener} to register
     */
    public void registerEvent(@NotNull(value = "listener cannot be null") final EventListener listener) {
        this.eventManager.registerEvents(listener, this.module);
    }

    /**
     * Unregisters every listener associated with the parent {@link Module}
     */
    public void unregisterAll() {
        this.eventManager.unregisterAll(this.module);
    }
}