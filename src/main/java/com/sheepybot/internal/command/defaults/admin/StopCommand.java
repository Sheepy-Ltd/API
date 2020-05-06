package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.ExitCode;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.utils.BotUtils;

public class StopCommand implements CommandExecutor {

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {
            context.reply("Shutting down...");
            if (args.hasFlag("-no-reboot")) {
                System.exit(ExitCode.EXIT_CODE_NORMAL);
            } else {
                System.exit(ExitCode.EXIT_CODE_RESTART);
            }
        }

        return true;
    }

}
