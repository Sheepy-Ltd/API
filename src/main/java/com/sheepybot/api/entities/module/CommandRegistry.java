package com.sheepybot.api.entities.module;

import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandHandler;
import com.sheepybot.api.entities.command.RootCommandRegistry;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CommandRegistry {

    private final RootCommandRegistry rootCommandRegistry;
    private final Module module;

    private CommandHandler handler;

    CommandRegistry(@NotNull("rootCommandRegistry cannot be null") final RootCommandRegistry rootCommandRegistry,
                    @NotNull("module cannot be null") final Module module) {
        this.rootCommandRegistry = rootCommandRegistry;
        this.module = module;
    }

    /**
     * @return The custom {@link CommandHandler} implementation or {@code null} if this {@link CommandRegistry} uses the default one.
     */
    public CommandHandler getCommandHandler() {
        return this.handler;
    }

    /**
     * Set the {@link CommandHandler} for this {@link CommandRegistry} to use in command processing.
     *
     * @param handler The {@link CommandHandler}
     */
    public void setCommandHandler(@NotNull("command handler cannot be null") final CommandHandler handler) {
        Objects.checkArgument(this.handler == null, "command handler is already set");
        this.handler = handler;
    }

    /**
     * Retrieve a command by its name or any aliases it may use
     *
     * @param aliases A name that may be used to execute a {@link Command}
     * @return The {@link Command} if it exists, {@code null} otherwise
     */
    public Command getCommandByNameOrAlias(@NotNull("command cannot be null") final List<String> aliases) {
        return this.rootCommandRegistry.getCommandByNameOrAlias(aliases);
    }

    /**
     * @param command The {@link Command} to register
     * @throws IllegalArgumentException If the {@link Command} is already registered
     */
    public void register(@NotNull("command cannot be null") final Command command) throws IllegalArgumentException {
        if (this.rootCommandRegistry.getCommandByNameOrAlias(command.getNames()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' already exists");
        }
        this.rootCommandRegistry.registerCommand(command, this.module);
    }

    /**
     * @param command The {@link Command} to unregisterCommand
     *
     * @throws IllegalArgumentException If the {@link Command} is not registered
     */
    public void unregister(@NotNull("command cannot be null") final Command command) throws IllegalArgumentException {
        if (this.rootCommandRegistry.getCommandByNameOrAlias(command.getNames()) != null) {
            throw new IllegalArgumentException("Command '" + command.getName() + "' is not registered");
        }
        this.rootCommandRegistry.unregisterCommand(command);
    }

    /**
     * Unregisters all commands registered by this {@link Module}
     */
    public void unregisterAll() {
        this.rootCommandRegistry.unregisterAll(this.module);
    }
}
