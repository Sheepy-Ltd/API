package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.server.member.message.MemberReactionAddEvent;
import com.sheepybot.api.entities.event.server.member.message.MemberReactionRemoveEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberReactionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event) {
        if (event.getJDA().getStatus() != JDA.Status.CONNECTED) return;

        final Guild guild = event.getGuild();
        final TextChannel channel = event.getChannel();

        if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_READ)) {
            return;
        }

        Bot.get().getEventRegistry().callEvent(new MemberReactionAddEvent(guild, channel, event.getMember(), event.getMessageIdLong(), event.getReaction(), event.getJDA()));
    }

    @Override
    public void onGuildMessageReactionRemove(final GuildMessageReactionRemoveEvent event) {
        if (event.getJDA().getStatus() != JDA.Status.CONNECTED) return;

        final Guild guild = event.getGuild();
        final TextChannel channel = event.getChannel();

        if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_READ)) {
            return;
        }

        Bot.get().getEventRegistry().callEvent(new MemberReactionRemoveEvent(guild, channel, event.getMember(), event.getMessageIdLong(), event.getReaction(), event.getJDA()));
    }

}
