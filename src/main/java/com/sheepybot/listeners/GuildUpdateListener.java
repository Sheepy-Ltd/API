package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.server.GuildUpdateIconEvent;
import com.sheepybot.api.entities.event.server.GuildUpdateNameEvent;
import com.sheepybot.api.entities.event.server.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildUpdateListener extends ListenerAdapter {

    @Override
    public void onGuildUpdateIcon(final net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildUpdateIconEvent(event.getOldIconUrl(), event.getNewIconUrl(), event.getGuild(), event.getJDA()));
    }

    @Override
    public void onGuildUpdateName(final net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildUpdateNameEvent(event.getOldName(), event.getNewName(), event.getGuild(), event.getJDA()));
    }

    @Override
    public void onGuildUpdateOwner(final net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildUpdateOwnerEvent(event.getOldOwner(), event.getNewOwner(), event.getGuild(), event.getJDA()));
    }

}
