package com.sheepybot.api.entities.event.guild.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called when a {@link Role} is deleted from a {@link Guild}
 */
public class GuildRoleDeleteEvent extends GuildRoleEvent {

    /**
     * @param guild The {@link Guild} the {@link Role} was deleted from
     * @param role  The {@link Role} deleted
     * @param jda   The {@link JDA} instance
     */
    public GuildRoleDeleteEvent(final Guild guild,
                                final Role role,
                                final JDA jda) {
        super(guild, role, jda);
    }

}
