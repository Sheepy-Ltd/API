package com.sheepybot.api.entities.command;

import com.google.common.collect.Lists;
import com.sheepybot.internal.command.DefaultCommandHandler;
import com.sheepybot.util.BotUtils;
import com.sheepybot.util.Objects;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class Command {

    private static final CommandHandler DEFAULT_COMMAND_HANDLER = new DefaultCommandHandler();

    private final List<String> names;
    private final String description;
    private final String usage;
    private final CommandExecutor executor;
    private final List<Permission> userPermissions;
    private final List<Permission> botPermissions;
    private final Function<CommandContext, Boolean> preExecutor;
    private final boolean isOwnerOnly;

    /**
     * @param names       The names and aliases of this {@link Command}
     * @param description The description of this {@link Command}
     * @param usage       The usage of this {@link Command}
     * @param executor    The {@link CommandExecutor} for this {@link Command}
     */
    private Command(@NotNull("names cannot be null") final List<String> names,
                    final String description,
                    final String usage,
                    @NotNull("executor cannot be null") final CommandExecutor executor,
                    @NotNull("userPermissions cannot be null") final List<Permission> userPermissions,
                    @NotNull("botPermissions cannot be null") final List<Permission> botPermissions,
                    @NotNull("preExecutor cannot be null") final Function<CommandContext, Boolean> preExecutor,
                    final boolean isOwnerOnly) {
        Objects.checkArgument(names.size() > 0, "names cannot be empty");
        this.names = names;
        this.description = description;
        this.usage = usage;
        this.executor = executor;
        this.userPermissions = userPermissions;
        this.botPermissions = botPermissions;
        this.preExecutor = preExecutor;
        this.isOwnerOnly = isOwnerOnly;
    }

    /**
     * @return The names of this {@link Command}
     */
    public final String getName() {
        return this.names.get(0);
    }

    /**
     * @return The names of this {@link Command}
     */
    public final List<String> getNames() {
        return Collections.unmodifiableList(this.names);
    }

    /**
     * @return The description of this {@link Command}
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * @return The usage of this {@link Command}
     */
    public final String getUsage() {
        return this.usage;
    }

    /**
     * @return A {@link List} of required {@link Permission}s for a {@link Member} to run this {@link Command}
     */
    public List<Permission> getUserPermissions() {
        return this.userPermissions;
    }

    /**
     * @return A list of required permissions for this {@link Command} to run
     */
    public List<Permission> getBotPermissions() {
        return this.botPermissions;
    }

    /**
     * Returns a function which should determine whether the command should
     * be executed.
     *
     * @return The pre executor
     */
    public Function<CommandContext, Boolean> getPreExecutor() {
        return this.preExecutor;
    }

    /**
     * @return The {@link CommandExecutor} for this {@link Command}
     */
    public CommandExecutor getExecutor() {
        return this.executor;
    }

    /**
     * Whether this command can only be ran by its owner.
     *
     * <p>This should be checked by a call to {@link BotUtils#isBotAdmin(Member)} which directly checks against the API configuration
     * for its list of bot admins.</p>
     *
     * @return {@code true} if this {@link Command} can only be ran by its owner.
     */
    public boolean isOwnerOnly() {
        return this.isOwnerOnly;
    }

    public void handle(@NotNull("context cannot be null") final CommandContext context,
                       @NotNull("args cannot be null") final Arguments args) {
        Command.DEFAULT_COMMAND_HANDLER.handle(context, args);
    }

    /**
     * Creates a new {@link Command.Builder}
     *
     * @return The {@link Command.Builder}
     */
    public static Builder builder() {
        return new Command.Builder();
    }

    /**
     *
     */
    public static final class Builder {

        private static final Function<CommandContext, Boolean> DEFAULT_PRE_EXECUTOR = context -> true;

        private List<String> names;
        private String description;
        private String usage;
        private CommandExecutor executor;
        private Function<CommandContext, Boolean> preExecutor = DEFAULT_PRE_EXECUTOR;
        private boolean isOwnerOnly = false;
        private final List<Permission> userPermissions;
        private final List<Permission> botPermissions;

        private Builder() {
            this.userPermissions = new ArrayList<>();
            this.botPermissions = new ArrayList<>();
        }


        /**
         * Set the unique names of this {@link Command}
         *
         * @param name The names of this {@link Command}
         * @return This {@link Builder}
         */
        public Builder names(@NotNull("command must have a names") final String name,
                             final String... aliases) {
            Objects.checkArgument(name.length() > 0, "command cannot have an effectively null names");

            this.names = Lists.newArrayList(name.toLowerCase());

            if (aliases != null) {
                Objects.checkNotNull(aliases, "cannot use null as an alias");
                Collections.addAll(this.names, aliases);
            }

            return this;
        }

        /**
         * @param description The description of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder description(@NotNull("description cannot be null") final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param usage The usage of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder usage(@NotNull("usage cannot be null") final String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * @param executor The executor for this {@link Command}
         * @return This {@link Builder}
         */
        public Builder executor(@NotNull("executor cannot be null") final CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Set a requirement of user permissions to execute this {@link Command}
         *
         * @param perm1       The first {@link Permission}
         * @param permissions Other {@link Permission}s
         * @return This {@link Builder}
         */
        public Builder userPermissions(@NotNull("perm1 cannot be null") final Permission perm1,
                                       final Permission... permissions) {

            if (permissions != null) {
                Objects.checkNotNull(permissions, "cannot use null as a permission");
                Collections.addAll(this.userPermissions, permissions);
            }

            this.userPermissions.add(perm1);

            return this;
        }

        /**
         * Set a requirement of bot permissions to execute this {@link Command}
         *
         * @param perm1       The first {@link Permission}
         * @param permissions Other {@link Permission}s
         * @return This {@link Builder}
         */
        public Builder botPermissions(@NotNull("perm1 cannot be null") final Permission perm1,
                                      final Permission... permissions) {

            if (permissions != null) {
                Objects.checkNotNull(permissions, "cannot use null as a permission");
                Collections.addAll(this.botPermissions, permissions);
            }

            this.botPermissions.add(perm1);

            return this;
        }

        /**
         * Returns a function which should determine whether the command should
         * be executed.
         *
         * @return This {@link Builder}
         */
        public Builder preExecutor(@NotNull("function cannot be null") final Function<CommandContext, Boolean> preExecutor) {
            this.preExecutor = preExecutor;
            return this;
        }

        /**
         * Set whether this {@link Command} can only be executed by its owner.
         *
         * <p>This value defaults to {@code false}</p>
         *
         * @param isOwnerOnly Whether this {@link Command} can only be executed by its owner.
         * @return This {@link Builder}
         */
        public Builder ownerOnly(final boolean isOwnerOnly) {
            this.isOwnerOnly = isOwnerOnly;
            return this;
        }

        /**
         * @return The {@link Command}
         * @throws IllegalArgumentException If there was no names specified for this {@link Command}
         * @throws NullPointerException     If this {@link Command} has no {@link CommandExecutor}
         */
        public Command build() {
            Objects.checkArgument(this.names.size() > 0, "command must have a names");
            Objects.checkNotNull(this.executor, "command executor cannot be null");
            return new Command(this.names, this.description, this.usage, this.executor, this.userPermissions,
                    this.botPermissions, this.preExecutor, this.isOwnerOnly);
        }

    }

}
