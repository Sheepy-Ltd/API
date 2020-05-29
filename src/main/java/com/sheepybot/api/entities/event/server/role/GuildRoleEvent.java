package com.sheepybot.api.entities.event.server.role;

import com.sheepybot.api.entities.event.server.GuildEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class GuildRoleEvent extends GuildEvent {

    private final Guild guild;
    private final Role role;
    private final JDA jda;

    /**
     * @param guild The {@link Guild} the {@link Role} was deleted from
     * @param role  The {@link Role} deleted
     * @param jda   The {@link JDA} instance
     */
    public GuildRoleEvent(final Guild guild,
                          final Role role,
                          final JDA jda) {
        super(guild, jda);
        this.guild = guild;
        this.role = role;
        this.jda = jda;
    }

    /**
     * @return The {@link Role} affected
     */
    public Role getRole() {
        return this.role;
    }

}
