package com.sheepybot.api.entities.module;

import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.api.entities.event.RegisteredListener;
import com.sheepybot.api.entities.event.RootEventRegistry;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class EventRegistry {

    private final RootEventRegistry eventManager;
    private final Module module;

    EventRegistry(@NotNull("eventManager cannot be null") final RootEventRegistry eventManager,
                  @NotNull("module cannot be null") final Module module) {
        this.eventManager = eventManager;
        this.module = module;
    }

    /**
     * @return A {@link Collection} containing every {@link RegisteredListener} associated with this {@link Module}
     */
    public Collection<RegisteredListener> getRegisteredListeners() {
        return this.eventManager.getRegisteredListeners(this.module);
    }

    /**
     * @param event The event to call
     */
    public void callEvent(@NotNull("event cannot be null") final Event event) {
        this.eventManager.callEvent(event);
    }

    /**
     * Registers all {@link Event}s in the given {@link EventListener} class
     *
     * @param listener The {@link EventListener} to register
     */
    public void registerEvent(@NotNull("listener cannot be null") final EventListener listener) {
        this.eventManager.registerEvents(listener, this.module);
    }

    /**
     * Unregisters every listener associated with the parent {@link Module}
     */
    public void unregisterAll() {
        this.eventManager.unregisterAll(this.module);
    }
}