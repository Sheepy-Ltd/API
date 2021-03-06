package com.sheepybot.api.entities.event;

import com.sheepybot.api.exception.event.EventException;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public interface EventExecutor {

    /**
     * @param listener The {@link EventListener} to this {@link Event}
     * @param event    The {@link Event} to execute
     * @throws EventException If an error occurred during the {@link Event}s execution
     */
    void execute(@NotNull("listener cannot be null") final EventListener listener,
                 @NotNull("event cannot be null") final GenericEvent event) throws EventException;
}
