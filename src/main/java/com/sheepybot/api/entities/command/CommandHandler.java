package com.sheepybot.api.entities.command;

import org.jetbrains.annotations.NotNull;

public interface CommandHandler {

    void handle(@NotNull("context cannot be null") final CommandContext context,
                @NotNull("args cannot be null") final Arguments args);

}
