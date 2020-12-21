package com.sheepybot.internal.command;

import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.UsageFormatter;
import com.sheepybot.api.entities.language.I18n;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public class DefaultUsageFormatter implements UsageFormatter {

    @Override
    public Message format(@NotNull("context be null") final Command command) {
        return new MessageBuilder().setContent(I18n.getDefaultI18n().tl("commandCorrectUsage", command.getName(), command.getUsage())).build();
    }

}
