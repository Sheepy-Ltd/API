package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.server.member.MemberLeaveEvent;
import com.sheepybot.api.entities.event.server.member.message.MemberMessageUpdateEvent;
import com.sheepybot.api.entities.event.server.member.message.MessageBulkDeleteEvent;
import com.sheepybot.api.entities.event.server.member.moderation.MemberBanEvent;
import com.sheepybot.api.entities.event.server.member.moderation.MemberPardonEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.function.Consumer;

public class GuildModerationListener extends ListenerAdapter {

    @Override
    public void onGuildMessageUpdate(final GuildMessageUpdateEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberMessageUpdateEvent(event.getGuild(), event.getChannel(), event.getMember(), event.getMessage(), event.getJDA()));
    }

    @Override
    public void onGuildBan(final GuildBanEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberBanEvent(event.getGuild(), event.getUser(), event.getJDA()));
    }

    @Override
    public void onGuildUnban(final GuildUnbanEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberPardonEvent(event.getGuild(), event.getUser(), event.getJDA()));
    }

    @Override
    public void onGuildMemberRemove(final GuildMemberRemoveEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberLeaveEvent(event.getGuild(), event.getUser(), event.getJDA()));
    }

    @Override
    public void onMessageBulkDelete(final net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent event) {
        Bot.get().getEventRegistry().callEvent(new MessageBulkDeleteEvent(event.getGuild(), event.getChannel(), event.getMessageIds(), event.getJDA()));
    }

    private void findAuditLogEntry(final Guild server,
                                   final long targetIdLong,
                                   final ActionType type,
                                   final Consumer<AuditLogEntry> auditEntry) {
        if (server.getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            server.retrieveAuditLogs().type(type).queue(entry -> entry.stream().filter(audit -> audit.getTargetIdLong() == targetIdLong).findFirst().ifPresent(auditEntry), __ -> {
            });
        }
    }

}
