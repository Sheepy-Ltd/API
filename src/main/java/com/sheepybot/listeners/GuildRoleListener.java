package com.sheepybot.listeners;

import com.sheepybot.Bot;
import com.sheepybot.api.entities.event.guild.role.*;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildRoleListener extends ListenerAdapter {

    @Override
    public void onRoleCreate(final RoleCreateEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildRoleCreateEvent(event.getGuild(), event.getRole(), event.getJDA()));
    }

    @Override
    public void onRoleDelete(final RoleDeleteEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildRoleDeleteEvent(event.getGuild(), event.getRole(), event.getJDA()));
    }

    @Override
    public void onRoleUpdateColor(final RoleUpdateColorEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildRoleUpdateColorEvent(event.getGuild(), event.getRole(), event.getOldColor(), event.getNewColor(), event.getJDA()));
    }

    @Override
    public void onRoleUpdateName(final RoleUpdateNameEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildRoleUpdateNameEvent(event.getGuild(), event.getRole(), event.getOldName(), event.getNewName(), event.getJDA()));
    }

    @Override
    public void onRoleUpdatePosition(final RoleUpdatePositionEvent event) {
        Bot.get().getEventRegistry().callEvent(new GuildRoleUpdatePositionEvent(event.getGuild(), event.getRole(), event.getOldPosition(), event.getNewPosition(), event.getJDA()));
    }



}
