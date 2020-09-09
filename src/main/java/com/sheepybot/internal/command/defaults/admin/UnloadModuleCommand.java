package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.entities.module.loader.ModuleLoader;
import com.sheepybot.util.BotUtils;

public class UnloadModuleCommand implements CommandExecutor {

    @Override
    public void execute(final CommandContext context,
                        final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply("You must be a bot admin to use this.");
        } else {

            final Module module = args.next(ReloadModuleCommand.MODULE_PARSER);

            final ModuleLoader loader = Bot.get().getModuleLoader();

            if (module.isEnabled()) loader.disableModule(module);
            loader.unloadModule(module);

            context.reply(String.format("Unloaded module %s, you can now safely replace the jar.", module.getFullName()));

        }

    }

}
