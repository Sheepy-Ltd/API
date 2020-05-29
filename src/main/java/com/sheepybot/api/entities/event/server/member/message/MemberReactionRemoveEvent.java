package com.sheepybot.api.entities.event.server.member.message;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

public class MemberReactionRemoveEvent extends MemberReactionEvent {

    /**
     * @param guild     The {@link Guild} this event was called in
     * @param channel   The {@link TextChannel} that this event was called in
     * @param messageId The {@link Message} ID the reaction was placed on
     * @param member    The {@link Member} who triggered this event
     * @param reaction  The {@link MessageReaction}
     * @param jda       The {@link JDA} instance
     */
    public MemberReactionRemoveEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                     @NotNull(value = "channel cannot be null") final TextChannel channel,
                                     final Member member,
                                     final long messageId,
                                     @NotNull(value = "reaction cannot be null") final MessageReaction reaction,
                                     @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, messageId, reaction, ReactionType.REMOVE, jda);
    }
}
