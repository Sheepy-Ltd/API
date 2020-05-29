package com.sheepybot.api.entities.event.server.member.message;

import com.sheepybot.api.entities.event.server.TextChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a member chats in a {@link TextChannel}
 */
public class MemberChatEvent extends TextChannelEvent {


    /**
     * @param guild   The {@link Guild} that this {@link Message} was sent in
     * @param member  The {@link Member} that triggered this event
     * @param channel The {@link TextChannel} that this {@link Message} was sent in
     * @param message The {@link Message} that was sent
     * @param jda     The {@link JDA} instance
     */
    public MemberChatEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                           @NotNull(value = "author cannot be null") final Member member,
                           @NotNull(value = "textChannel cannot be null") final TextChannel channel,
                           @NotNull(value = "message cannot be null") final Message message,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, message, message.getIdLong(), jda);
    }

}
