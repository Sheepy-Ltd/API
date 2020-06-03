package com.sheepybot.api.entities.command;

public interface CommandExecutor {

    /**
     * @param context The {@link CommandContext} to handle
     * @param args    The {@link Arguments} passed with the {@link CommandContext}
     */
    void execute(final CommandContext context,
                 final Arguments args);

}
