package com.sheepybot.api.entities.event.guild.member.message;

import com.sheepybot.api.entities.event.guild.TextChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Member} updates their message
 */
public class MemberMessageUpdateEvent extends TextChannelEvent {

    /**
     * @param guild   The {@link Guild} this event was triggered in
     * @param channel The {@link TextChannel} the event was triggered in
     * @param member  The {@link Member} who triggered this event
     * @param message The {@link Message} affected
     * @param jda     The {@link JDA} instance
     */
    public MemberMessageUpdateEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                    @NotNull(value = "channel cannot be null") final TextChannel channel,
                                    final Member member,
                                    @NotNull(value = "message cannot be null") final Message message,
                                    @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, message, message.getIdLong(), jda);
    }

}
