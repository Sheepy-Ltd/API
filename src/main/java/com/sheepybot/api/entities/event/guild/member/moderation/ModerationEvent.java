package com.sheepybot.api.entities.event.guild.member.moderation;

import com.sheepybot.api.entities.event.guild.member.MemberEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * Called whenever a moderation action is performed on a {@link User}
 *
 * <p>These actions include</p>
 * <ul>
 *     <li>Ban</li>
 *     <li>Un-ban</li>
 * </ul>
 */
public class ModerationEvent extends MemberEvent {

    /**
     * @param guild The {@link Guild} this event was triggered in
     * @param user  The {@link User} affected by this event
     * @param jda   The {@link JDA} instance
     */
    public ModerationEvent(final Guild guild,
                           final User user,
                           final JDA jda) {
        super(guild, null, user, user.getIdLong(), jda);
    }

}
