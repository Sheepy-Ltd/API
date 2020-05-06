package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.event.server.member.MemberJoinEvent;
import com.sheepybot.api.event.server.member.MemberLeaveEvent;
import com.sheepybot.api.event.server.member.MemberNicknameChangeEvent;
import com.sheepybot.api.event.server.member.role.MemberRolesAddedEvent;
import com.sheepybot.api.event.server.member.role.MemberRolesRemovedEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import org.jetbrains.annotations.NotNull;

public class GuildMemberListener extends BotListener {

    public GuildMemberListener(@NotNull(value = "parent cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        this.getEventManager().callEvent(new MemberJoinEvent(event.getGuild(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onGuildMemberUpdateNickname(final GuildMemberUpdateNicknameEvent event) {
        this.getEventManager().callEvent(new MemberNicknameChangeEvent(event.getMember(), event.getGuild(), event.getOldNickname(), event.getNewNickname(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        this.getEventManager().callEvent(new MemberRolesAddedEvent(event.getMember(), event.getGuild(), event.getRoles(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        this.getEventManager().callEvent(new MemberRolesRemovedEvent(event.getMember(), event.getGuild(), event.getRoles(), event.getJDA()));
    }

}
