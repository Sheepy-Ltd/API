package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.command.parsers.ArgumentParsers;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.entities.module.loader.ModuleLoader;
import com.sheepybot.api.exception.module.InvalidModuleException;
import com.sheepybot.util.BotUtils;

import java.io.File;

public class LoadModuleCommand implements CommandExecutor {

    @Override
    public void execute(final CommandContext context,
                        final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply("You must be a bot admin to use this.");
        } else {

            final File file = new File("modules", args.next(ArgumentParsers.REMAINING_STRING) + ".jar");
            if (!file.exists()) {
                context.reply("That module file doesn't exist, have you checked your casing?");
            } else {

                final ModuleLoader loader = Bot.get().getModuleLoader();
                try {
                    final Module module = loader.loadModule(file);
                    loader.enableModule(module);
                    context.reply(String.format("Loaded and enabled module %s", module.getFullName()));
                } catch (final InvalidModuleException ex) {
                    context.reply(String.format("Couldn't load module: %s", ex.getMessage()));
                }

            }

        }

    }

}
