package com.sheepybot.api.entities.command;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public interface UsageFormatter {

    Message format(@NotNull("context be null") final Command command);

}
