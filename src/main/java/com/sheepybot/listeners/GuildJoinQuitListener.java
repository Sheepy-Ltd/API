package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildJoinQuitListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildJoinQuitListener.class);

    @Override
    public void onGuildReady(final GuildReadyEvent event) {

        final Guild guild = event.getGuild();
        final JDA jda = event.getJDA();

        LOGGER.info(String.format("Joined guild %s(%s)", guild.getName(), guild.getId()));

//            final Document document = new Document();
//            document.put("server_id", guild.getId());
//            document.put("server_name", guild.getName());
//            document.put("server_avatar", guild.getIconUrl());
//            document.put("server_owner", guild.getOwnerId());
//            document.put("server_region", guild.getRegion().getName());
//            document.put("server_region_id", guild.getRegion().getKey());
//            document.put("server_is_vip", guild.getRegion().isVip());
//
//            final VoiceChannel afk = guild.getAfkChannel();
//            if (afk == null) {
//                document.put("server_afk_channel", null);
//            } else {
//                document.put("server_afk_channel", afk.getId());
//            }
//
//            document.put("server_afk_timeout", guild.getAfkTimeout().getSeconds());
//
//            final TextChannel system = guild.getSystemChannel();
//            if (system == null) {
//                document.put("server_system_channel", null);
//            } else {
//                document.put("server_system_channel", system.getId());
//            }
//
//            //noinspection ConstantConditions
//            document.put("server_default_channel", guild.getDefaultChannel().getId());
//
//            document.put("server_verification_level", guild.getVerificationLevel().getKey());
//            document.put("server_mfa_level", guild.getRequiredMFALevel().getKey());
//            document.put("server_explicit_content_level", guild.getExplicitContentLevel().getKey());
//
//            this.servers.insertOne(document);

    }

    @Override
    public void onGuildLeave(final net.dv8tion.jda.api.events.guild.GuildLeaveEvent event) {

        final Guild server = event.getGuild();

        LOGGER.info(String.format("Left guild %s(%s)", server.getName(), server.getId()));

        Bot.get().getEventRegistry().callEvent(new GuildLeaveEvent(server, event.getJDA()));
    }

}
