package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.server.member.MemberJoinEvent;
import com.sheepybot.api.entities.event.server.member.MemberLeaveEvent;
import com.sheepybot.api.entities.event.server.member.MemberNicknameChangeEvent;
import com.sheepybot.api.entities.event.server.member.role.MemberRolesAddedEvent;
import com.sheepybot.api.entities.event.server.member.role.MemberRolesRemovedEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(final GuildMemberJoinEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberJoinEvent(event.getGuild(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRemove(final GuildMemberRemoveEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberLeaveEvent(event.getGuild(), event.getUser(), event.getJDA()));
    }

    @Override
    public void onGuildMemberUpdateNickname(final GuildMemberUpdateNicknameEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberNicknameChangeEvent(event.getGuild(), event.getMember(), event.getOldNickname(), event.getNewNickname(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleAdd(final GuildMemberRoleAddEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberRolesAddedEvent(event.getGuild(), event.getMember(), event.getRoles(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRoleRemove(final GuildMemberRoleRemoveEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberRolesRemovedEvent(event.getGuild(), event.getMember(), event.getRoles(), event.getJDA()));
    }

}
