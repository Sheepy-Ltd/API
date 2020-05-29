package com.sheepybot.api.entities.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class TextChannelEvent extends GuildEvent {

    private final Member member;
    private final TextChannel channel;
    private final Message message;
    private final long messageId;

    /**
     * @param guild     The {@link Guild} this event was triggered in
     * @param channel   The {@link TextChannel} this event was triggered in
     * @param member    The {@link Member} who triggered this event
     * @param message   The {@link Message}
     * @param messageId The message ID
     * @param jda       The {@link JDA} instance
     */
    public TextChannelEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                            @NotNull(value = "channel cannot be null") final TextChannel channel,
                            final Member member,
                            final Message message,
                            final long messageId,
                            @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, jda);
        this.member = member;
        this.channel = channel;
        this.message = message;
        this.messageId = messageId;
    }

    /**
     * @return The possibly null {@link Member} who triggered this {@link TextChannelEvent}
     */
    public Member getMember() {
        return this.member;
    }

    /**
     * @return The {@link TextChannel} this event was triggered in
     */
    public TextChannel getChannel() {
        return this.channel;
    }

    /**
     * @return The possibly null {@link Message}
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * @return The message ID
     */
    public long getMessageId() {
        return this.messageId;
    }

}
