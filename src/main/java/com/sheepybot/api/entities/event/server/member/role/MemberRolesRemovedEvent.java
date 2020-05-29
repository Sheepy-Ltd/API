package com.sheepybot.api.entities.event.server.member.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MemberRolesRemovedEvent extends MemberRoleEvent {

    /**
     * @param guild  The {@link Guild} that this event was triggered in
     * @param member The {@link Member} affected by this event
     * @param roles  A {@link Collection} of roles removed from the {@link Member}
     * @param jda    The {@link JDA} instance
     */
    public MemberRolesRemovedEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                   @NotNull(value = "member cannot be null") final Member member,
                                   @NotNull(value = "roles cannot be null") final Collection<Role> roles,
                                   @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, member, roles, jda);
    }

}
