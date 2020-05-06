package com.sheepybot.api.event.command;

import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.event.Cancellable;
import com.sheepybot.api.event.server.member.message.MessageEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a {@link Command}'s processing and before even being verified as a command.
 */
public class CommandPreProcessEvent extends MessageEvent<TextChannel> implements Cancellable {

    private final String command;
    private final String[] arguments;
    private boolean isCancelled;

    /**
     * @param member        The {@link User} that executed this {@link Command}
     * @param server      The {@link Guild} that this {@link Command} was executed in
     * @param channel The {@link TextChannel} that this {@link Command} was executed in
     * @param command     The {@link String} that was executed
     * @param arguments   The arguments executed with this command
     */
    public CommandPreProcessEvent(@NotNull(value = "user cannot be null") final Member member,
                                  final Guild server,
                                  final TextChannel channel,
                                  final Message message,
                                  @NotNull(value = "command cannot be null") final String command,
                                  final String[] arguments,
                                  @NotNull(value = "jda cannot be null") final JDA jda) {
        super(member, member.getUser(), server, channel, message, jda);
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * @return The {@link Command} that was executed
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return The arguments executed with this command
     */
    public String[] getArguments() {
        return this.arguments;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

}
