package com.sheepybot.api.entities.command;

import com.google.common.collect.Lists;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class Command {

    private final List<String> names;
    private final String description;
    private final String usage;
    private final CommandExecutor executor;

    /**
     * @param names       The names and aliases of this {@link Command}
     * @param description The description of this {@link Command}
     * @param usage       The usage of this {@link Command}
     * @param executor    The {@link CommandExecutor} for this {@link Command}
     */
    private Command(@NotNull(value = "names cannot be null") final List<String> names,
                    final String description,
                    final String usage,
                    @NotNull(value = "executor cannot be null") final CommandExecutor executor) {
        Objects.checkArgument(names.size() > 0, "names cannot be empty");
        this.names = names;
        this.description = description;
        this.usage = usage;
        this.executor = executor;
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
     * @return The {@link CommandExecutor} for this {@link Command}
     */
    public CommandExecutor getExecutor() {
        return this.executor;
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

        private List<String> names;
        private String description;
        private String usage;
        private CommandExecutor executor;


        /**
         * Set the unique names of this {@link Command}
         *
         * @param name The names of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder names(@NotNull(value = "command must have a names") final String name,
                             final String... aliases) {
            Objects.checkArgument(name.length() > 0, "command cannot have an effectively null names");

            this.names = Lists.newArrayList(name.toLowerCase());

            final String[] newAliases = new String[names.size()];
            for (int i = 0; i < aliases.length; i++) { //force lower case
                newAliases[i] = aliases[i].toLowerCase();
            }

            Collections.addAll(this.names, newAliases);

            return this;
        }

        /**
         * @param description The description of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder description(@NotNull(value = "description cannot be null") final String description) {
            this.description = description;
            return this;
        }

        /**
         * @param usage The usage of this {@link Command}
         *
         * @return This {@link Builder}
         */
        public Builder usage(@NotNull(value = "usage cannot be null") final String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * @param executor The executor for this {@link Command}
         *
         * @return This {@link Command.Builder}
         */
        public Builder executor(@NotNull(value = "executor cannot be null") final CommandExecutor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * @return The {@link Command}
         *
         * @throws IllegalArgumentException If there was no names specified for this {@link Command}
         * @throws NullPointerException     If this {@link Command} has no {@link CommandExecutor}
         */
        public Command build() {
            Objects.checkArgument(this.names.size() > 0, "command must have a names");
            Objects.checkNotNull(this.executor, "command executor cannot be null");
            return new Command(this.names, this.description, this.usage, this.executor);
        }

    }

}
