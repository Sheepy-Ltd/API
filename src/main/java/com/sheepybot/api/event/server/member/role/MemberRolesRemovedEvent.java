package com.sheepybot.api.event.server.member.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collection;

public class MemberRolesRemovedEvent extends MemberRoleEvent {

    public MemberRolesRemovedEvent(final Member member,
                                   final Guild server,
                                   final Collection<Role> roles,
                                   final JDA jda) {
        super(member, server, roles, jda);
    }

}
