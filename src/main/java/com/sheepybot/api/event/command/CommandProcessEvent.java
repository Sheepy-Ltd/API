package com.sheepybot.api.event.command;

import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a {@link Command} has been validated and before its execution
 */
public class CommandProcessEvent extends Event {

    private final CommandContext context;
    private final Arguments arguments;

    /**
     * @param context The {@link CommandContext}
     */
    public CommandProcessEvent(@NotNull(value = "context cannot be null") final CommandContext context,
                               @NotNull(value = "args cannot be null") final Arguments arguments) {
        this.context = context;
        this.arguments = arguments;
    }

    /**
     * @return The {@link CommandContext}
     */
    public CommandContext getContext() {
        return this.context;
    }

    /**
     * @return The {@link Arguments} executed with the {@link Command}
     */
    public Arguments getArguments() {
        return this.arguments;
    }
}
