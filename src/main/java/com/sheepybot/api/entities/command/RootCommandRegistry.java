package com.sheepybot.api.entities.command;

import com.sheepybot.api.entities.module.Module;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * The {@link RootCommandRegistry} is responsible for holding data
 * of all registered {@link Command}'s.
 */
public interface RootCommandRegistry {

    Logger LOGGER = LoggerFactory.getLogger(RootCommandRegistry.class);

    /**
     * @return This servers command map
     */
    Map<Module, List<Command>> getCommandMap();

    /**
     * Retrieve a {@link Command} by its name or any alias it may use to be referenced or executed
     *
     * @param aliases The names a command may used to be executed
     * @return The {@link Command}, or {@code null} if no command was found
     */
    Command getCommandByNameOrAlias(@NotNull("aliases cannot be null") final List<String> aliases);

    /**
     * Register a {@link Command} in this {@link RootCommandRegistry}
     *
     * @param command The {@link Command} to register
     * @param module  The owning {@link Module}.
     */
    void registerCommand(@NotNull("command cannot be null") final Command command,
                         final Module module);

    /**
     * Un-register a {@link Command} from this command map
     *
     * @param command The {@link Command} to un-register
     */
    void unregisterCommand(@NotNull("command cannot be null") final Command command);

    /**
     * Un-register all {@link Command}'s associated with the given {@link Module}
     *
     * @param module The {@link Module} to un-register commands from.
     */
    void unregisterAll(@NotNull("parent module cannot be null") final Module module);

    /**
     * Un-register all {@link Command}'s in this command map
     */
    void unregisterAll();
}
