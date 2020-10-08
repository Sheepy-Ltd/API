package com.sheepybot.api.entities.command;

import org.jetbrains.annotations.NotNull;

public interface CommandHandler {

    void handle(@NotNull(value = "context cannot be null") final CommandContext context,
                @NotNull(value = "args cannot be null") final Arguments args);

}
