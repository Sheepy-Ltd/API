package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.command.argument.ArgumentParser;
import com.sheepybot.api.entities.command.argument.RawArguments;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.entities.module.loader.ModuleLoader;
import com.sheepybot.api.exception.module.InvalidModuleException;
import com.sheepybot.api.exception.parser.ParserException;
import com.sheepybot.util.BotUtils;

import java.io.File;

public class ReloadModuleCommand implements CommandExecutor {

    @Override
    public void execute(final CommandContext context,
                        final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply("You must be a bot admin to use this.");
        } else {

            final Module module = args.next(MODULE_PARSER);

            if (!module.isEnabled()) {
                context.reply("That module isn't enabled.");
            } else {

                final String oldVersion = module.getData().version();

                context.reply(String.format("Reloading module %s...", module.getName()));

                final File moduleFile = new File(module.getJar().getAbsolutePath());

                final ModuleLoader loader = Bot.get().getModuleLoader();
                loader.disableModule(module);
                loader.unloadModule(module);

                try {
                    final Module reloadedModule = loader.loadModule(moduleFile);
                    loader.enableModule(reloadedModule);
                    context.reply(String.format("Reloaded module %s! Now running version %s (was %s)", reloadedModule.getName(), reloadedModule.getData().version(), oldVersion));
                } catch (final InvalidModuleException ignored) {
                    context.reply("Couldn't load the module");
                }

            }

        }

    }

    public static final ArgumentParser<Module> MODULE_PARSER = new ArgumentParser<Module>() {
        @Override
        public Module parse(final CommandContext context,
                            final RawArguments args) {

            if (args.peek() == null) {
                return null;
            }

            final Module module = Bot.get().getModuleLoader().getModuleByName(args.next());
            if (module == null) {
                throw new ParserException("Unknown module.");
            }

            return module;
        }
    };

}
