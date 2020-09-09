package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.entities.module.loader.ModuleLoader;
import com.sheepybot.util.BotUtils;

public class DisableModuleCommand implements CommandExecutor {

    @Override
    public void execute(final CommandContext context,
                        final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply("You must be a bot admin to use this.");
        } else {

            final Module module = args.next(ReloadModuleCommand.MODULE_PARSER);

            if (!module.isEnabled()) {
                context.reply("That module is already disabled.");
            } else {

                final ModuleLoader loader = Bot.get().getModuleLoader();
                loader.disableModule(module);

                context.reply(String.format("Disabled module %s.", module.getFullName()));

            }

        }

    }

}
