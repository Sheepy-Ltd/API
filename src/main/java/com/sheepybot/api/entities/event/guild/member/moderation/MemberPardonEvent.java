package com.sheepybot.api.entities.event.guild.member.moderation;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class MemberPardonEvent extends ModerationEvent {

    /**
     * @param guild The {@link Guild} this event was triggered in
     * @param user  The {@link User} who has been unbanned
     * @param jda   The {@link JDA} instance
     */
    public MemberPardonEvent(final Guild guild,
                             final User user,
                             final JDA jda) {
        super(guild, user, jda);
    }

}
