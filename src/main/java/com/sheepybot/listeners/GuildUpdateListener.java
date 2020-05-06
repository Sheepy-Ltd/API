package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.event.server.ServerUpdateIconEvent;
import com.sheepybot.api.event.server.ServerUpdateNameEvent;
import com.sheepybot.api.event.server.ServerUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import org.jetbrains.annotations.NotNull;

public class GuildUpdateListener extends BotListener {

    public GuildUpdateListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        super(bot);
    }

    @Override
    public void onGuildUpdateIcon(final GuildUpdateIconEvent event) {
        this.getEventManager().callEvent(new ServerUpdateIconEvent(event.getOldIconUrl(), event.getNewIconUrl(), event.getGuild(), event.getJDA()));
    }

    @Override
    public void onGuildUpdateName(final GuildUpdateNameEvent event) {
        this.getEventManager().callEvent(new ServerUpdateNameEvent(event.getOldName(), event.getNewName(), event.getGuild(), event.getJDA()));
    }

    @Override
    public void onGuildUpdateOwner(final GuildUpdateOwnerEvent event) {
        this.getEventManager().callEvent(new ServerUpdateOwnerEvent(event.getOldOwner(), event.getNewOwner(), event.getGuild(), event.getJDA()));
    }

}
