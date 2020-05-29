package com.sheepybot.api.entities.event.server.member.role;

import com.sheepybot.api.entities.event.server.member.MemberEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MemberRoleEvent extends MemberEvent {

    private final Collection<Role> roles;

    /**
     * @param guild  The {@link Guild} this event was triggered in
     * @param member The {@link Member} that this event was triggered by
     * @param roles  A {@link Collection} of {@link Role}s in the event
     * @param jda    The {@link JDA} instance
     */
    public MemberRoleEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                           @NotNull(value = "member cannot be null") final Member member,
                           @NotNull(value = "roles cannot be null") final Collection<Role> roles,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, member, member.getUser(), member.getUser().getIdLong(), jda);
        this.roles = roles;
    }

    /**
     * @return A {@link Collection} of {@link Role}s in the event
     */
    public Collection<Role> getRoles() {
        return this.roles;
    }
}
