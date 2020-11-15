package com.sheepybot.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.RootCommandRegistry;
import com.sheepybot.api.entities.module.Module;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandRegistryImpl implements RootCommandRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistryImpl.class);

    private final Map<Module, List<Command>> commandMap;

    public CommandRegistryImpl() {
        this.commandMap = Maps.newHashMap();
    }

    @Override
    public Map<Module, List<Command>> getCommandMap() {
        return this.commandMap;
    }

    @Override
    public Command getCommandByNameOrAlias(@NotNull("alias cannot be null") final List<String> aliases) {
        LOGGER.debug("Retrieving all commands with any name/alias matching {}...", Arrays.toString(aliases.toArray()));
        return this.commandMap.values().stream().flatMap(commands -> commands.stream().filter(command -> command.getNames().stream().anyMatch(aliases::contains))).findFirst().orElse(null);
    }

    @Override
    public void registerCommand(@NotNull("command cannot be null") final Command command,
                                final Module module) {
        LOGGER.debug("Registering command {} in module {} to root command registry...", command.getName(), (module == null ? "Bot" : module.getName()));
        if (this.getCommandByNameOrAlias(command.getNames()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' is already registered");
        }
        final List<Command> commands = this.commandMap.computeIfAbsent(module, __ -> Lists.newArrayList());
        commands.add(command);
    }

    @Override
    public void unregisterCommand(@NotNull("command cannot be null") final Command command) {
        LOGGER.debug("Unregistering command {}...", command.getName());
        this.commandMap.keySet().forEach(module -> this.commandMap.get(module).removeIf(cmd -> cmd.getName().equalsIgnoreCase(command.getName())));
    }

    @Override
    public void unregisterAll(@NotNull("module cannot be null") final Module module) {
        LOGGER.debug("Unregistering all commands from module {}...", module.getName());
        this.commandMap.remove(module);
    }

    @Override
    public void unregisterAll() {
        LOGGER.debug("Unregistering all commands from root command registry...");
        this.commandMap.clear();
    }

}
