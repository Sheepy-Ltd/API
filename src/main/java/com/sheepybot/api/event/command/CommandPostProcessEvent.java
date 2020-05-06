package com.sheepybot.api.event.command;

import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a {@link Command} has finished an error-less execution
 */
public class CommandPostProcessEvent extends Event {

    private final CommandContext context;
    private final boolean success;

    public CommandPostProcessEvent(@NotNull(value = "context cannot be null") final CommandContext context,
                                   final boolean success) {
        this.context = context;
        this.success = success;
    }

    /**
     * @return The {@link CommandContext} executed
     */
    public CommandContext getContext() {
        return this.context;
    }

    /**
     * @return {@code true} if the {@link CommandContext} was successfully executed
     */
    public boolean isSuccess() {
        return this.success;
    }
}
