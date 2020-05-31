package com.sheepybot.api.entities.event.guild.member.moderation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Called whenever a {@link User} is banned from a {@link Guild}
 */
public class MemberBanEvent extends ModerationEvent {

    /**
     * @param guild The {@link Guild} this event was triggered in
     * @param user  The {@link User} affected by this event
     * @param jda   The {@link JDA} instance
     */
    public MemberBanEvent(final Guild guild,
                          final User user,
                          final JDA jda) {
        super(guild, user, jda);
    }

}
