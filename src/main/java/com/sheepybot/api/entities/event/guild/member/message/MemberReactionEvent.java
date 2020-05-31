package com.sheepybot.api.entities.event.guild.member.message;

import com.sheepybot.api.entities.event.guild.TextChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

public class MemberReactionEvent extends TextChannelEvent {

    private final MessageReaction reaction;
    private final ReactionType reactionType;

    /**
     * @param guild        The {@link Guild} this event was called in
     * @param channel      The {@link TextChannel} that this event was called in
     * @param member       The {@link Member} who triggered the event
     * @param messageId    The {@link Message} ID the reaction was placed on
     * @param reaction     The {@link MessageReaction}
     * @param reactionType The {@link MemberReactionEvent.ReactionType}
     * @param jda          The {@link JDA} instance
     */
    public MemberReactionEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                               @NotNull(value = "channel cannot be null") final TextChannel channel,
                               final Member member,
                               final long messageId,
                               @NotNull(value = "reaction cannot be null") final MessageReaction reaction,
                               @NotNull(value = "reaction type cannot be null") final ReactionType reactionType,
                               @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, null, messageId, jda);
        this.reaction = reaction;
        this.reactionType = reactionType;
    }

    /**
     * @return The {@link MessageReaction}
     */
    public MessageReaction getReaction() {
        return this.reaction;
    }

    /**
     * @return The {@link ReactionType}
     */
    public ReactionType getReactionType() {
        return this.reactionType;
    }

    public enum ReactionType {

        ADD,
        REMOVE,
        REMOVE_ALL

    }
}
