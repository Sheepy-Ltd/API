package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.server.member.voice.*;
import com.sheepybot.api.entities.event.server.voice.VoiceChannelCreateEvent;
import com.sheepybot.api.entities.event.server.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildMemberVoiceListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMemberVoiceListener.class);

    @Override
    public void onGuildVoiceJoin(final GuildVoiceJoinEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberVoiceJoinEvent(event.getGuild(), event.getChannelJoined(), event.getMember(), event.getJDA()));

    }

    @Override
    public void onGuildVoiceMove(final GuildVoiceMoveEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberVoiceMoveEvent(event.getGuild(), event.getMember(), event.getChannelJoined(), event.getChannelLeft(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberVoiceLeaveEvent(event.getGuild(), event.getChannelLeft(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceMute(final GuildVoiceMuteEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberVoiceMuteEvent(event.getGuild(), event.getVoiceState().getChannel(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onGuildVoiceDeafen(final GuildVoiceDeafenEvent event) {
        Bot.get().getEventRegistry().callEvent(new MemberVoiceDeafenEvent(event.getGuild(), event.getVoiceState().getChannel(), event.getMember(), event.getJDA()));
    }

    @Override
    public void onVoiceChannelCreate(final net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent event) {
        Bot.get().getEventRegistry().callEvent(new VoiceChannelCreateEvent(event.getGuild(), event.getChannel(), event.getJDA()));
    }

    @Override
    public void onVoiceChannelDelete(final net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent event) {
        Bot.get().getEventRegistry().callEvent(new VoiceChannelDeleteEvent(event.getGuild(), event.getChannel(), event.getJDA()));
    }
}
