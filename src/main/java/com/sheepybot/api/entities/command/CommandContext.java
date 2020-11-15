package com.sheepybot.api.entities.command;

import com.sheepybot.api.entities.language.I18n;
import com.sheepybot.api.entities.messaging.Messaging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

public class CommandContext {

    private final TextChannel channel;
    private final Member sender;
    private final Guild guild;
    private final Message message;
    private final String trigger;
    private final Command command;
    private final I18n i18n;
    private final JDA jda;

    /**
     * @param channel The channel this {@link Command} was executed in
     * @param sender  The {@link User} who executed this {@link Command}
     * @param guild   The {@link Guild} this command was executed in
     * @param trigger The command trigger
     * @param command The {@link Command} executed
     * @param message The whole message that caused this {@link Command} to trigger
     */
    public CommandContext(@NotNull("channel cannot be null") final TextChannel channel,
                          @NotNull("sender cannot be null") final Member sender,
                          @NotNull("guild cannot be null") final Guild guild,
                          @NotNull("trigger cannot be null") final String trigger,
                          @NotNull("command cannot be null") final Command command,
                          @NotNull("message cannot be null") final Message message,
                          @NotNull("i18n cannot be null") final I18n i18n,
                          @NotNull("jda cannot be null") final JDA jda) {
        this.channel = channel;
        this.sender = sender;
        this.guild = guild;
        this.trigger = trigger;
        this.command = command;
        this.message = message;
        this.i18n = i18n;
        this.jda = jda;
    }

    /**
     * @return The {@link TextChannel} this command was executed in (can be null)
     */
    public TextChannel getChannel() {
        return this.channel;
    }

    /**
     * @return The {@link User} who executed this command
     */
    public User getUser() {
        return this.sender.getUser();
    }

    /**
     * @return The executor of this {@link Command} as a {@link Member}
     */
    public Member getMember() {
        return this.sender;
    }

    /**
     * @return The self guild {@link Member}
     */
    public Member getSelfMember() {
        return this.guild.getSelfMember();
    }

    /**
     * @return The {@link Guild} this command was executed in (can be null)
     */
    public Guild getGuild() {
        return this.guild;
    }

    /**
     * @return The command trigger
     */
    public String getLabel() {
        return this.trigger;
    }

    /**
     * @return The {@link Command} executed
     */
    public Command getCommand() {
        return this.command;
    }

    /**
     * @return The whole message that caused this {@link Command} to trigger
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * @return The {@link JDA} instance
     */
    public JDA getJDA() {
        return this.jda;
    }

    /**
     * @return The {@link I18n} instance
     */
    public I18n getI18n() {
        return this.i18n;
    }

    /**
     * Translate the input string
     *
     * @param key  The message key
     * @param args The arguments
     *
     * @return The translated string
     */
    public String i18n(final String key, final Object... args) {
        return this.i18n.tl(key, args);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param message The message to send
     */
    public void reply(@NotNull("message cannot be null") final String message) {
        Messaging.send(this.channel, message);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param embed The {@link MessageEmbed} to sent
     */
    public void reply(@NotNull("embed cannot be null") final MessageEmbed embed) {
        Messaging.send(this.channel, embed);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param message The {@link Message} to send
     */
    public void reply(@NotNull("message cannot be null") final Message message) {
        Messaging.send(this.channel, message);
    }

    /**
     * Creates a new {@link com.sheepybot.api.entities.messaging.Messaging.MessageActionBuilder} to send a message in the current channel.
     *
     * @param message The message to send.
     * @return A {@link com.sheepybot.api.entities.messaging.Messaging.MessageActionBuilder}
     */
    public Messaging.MessageActionBuilder message(final String message) {
        return Messaging.message(this.channel, message);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param embed The {@link MessageEmbed} to sent
     * @return A {@link com.sheepybot.api.entities.messaging.Messaging.MessageActionBuilder}
     */
    public Messaging.MessageActionBuilder message(@NotNull("embed cannot be null") final MessageEmbed embed) {
        return Messaging.message(this.channel, embed);
    }

    /**
     * Sends a message in the {@link MessageChannel} this command was executed in
     *
     * @param message The {@link Message} to send
     * @return A {@link com.sheepybot.api.entities.messaging.Messaging.MessageActionBuilder}
     */
    public Messaging.MessageActionBuilder message(@NotNull("message cannot be null") final Message message) {
        return Messaging.message(this.channel, message);
    }

}
