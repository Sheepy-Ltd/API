package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.Bot;
import com.sheepybot.ExitCode;
import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.utils.BotUtils;

public class UpdateCommand implements CommandExecutor {

    private final Bot bot;

    public UpdateCommand(@NotNull(value = "bot cannot be null") final Bot bot) {
        this.bot = bot;
    }

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {
            context.reply("Updating current build...");
            System.exit(ExitCode.EXIT_CODE_UPDATE);
        }

        return true;
    }

}
