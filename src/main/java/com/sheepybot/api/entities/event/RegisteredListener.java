package com.sheepybot.api.entities.event;

import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.exception.event.EventException;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;

public class RegisteredListener {

    private final EventListener listener;
    private final EventExecutor executor;
    private final EventHandler handler;
    private final EventHandler.EventPriority priority;
    private final Module module;

    /**
     * @param listener The {@link EventListener} to the {@link Event}
     * @param executor The {@link EventExecutor} for this {@link Event}
     * @param handler  The {@link EventHandler} for this {@link Event}
     * @param module   The {@link Module} to register this {@link RegisteredListener} to
     */
    public RegisteredListener(@NotNull("listener cannot be null") final EventListener listener,
                              @NotNull("executor cannot be null") final EventExecutor executor,
                              @NotNull("handler cannot be null") final EventHandler handler,
                              @NotNull("parent module cannot be null") final Module module) {
        this.listener = listener;
        this.executor = executor;
        this.handler = handler;
        this.priority = handler.priority();
        this.module = module;
    }

    /**
     * @return The listening class of this {@link Event}
     */
    public EventListener getListener() {
        return this.listener;
    }

    /**
     * @return The {@link Module} this {@link RegisteredListener} is registered to
     */
    public Module getModule() {
        return this.module;
    }

    /**
     * @return The {@link EventExecutor} for this {@link Event}
     */
    public EventExecutor getExecutor() {
        return this.executor;
    }

    /**
     * @return The {@link EventHandler}
     */
    public EventHandler getHandler() {
        return this.handler;
    }

    /**
     * @return The event priority
     */
    public EventHandler.EventPriority getPriority() {
        return this.priority;
    }

    /**
     * @param event The {@link Event} to call
     * @throws EventException If an error occurred during event execution
     */
    public void callEvent(@NotNull("event cannot be null") final GenericEvent event) throws EventException {
        this.executor.execute(this.listener, event);
    }

}
