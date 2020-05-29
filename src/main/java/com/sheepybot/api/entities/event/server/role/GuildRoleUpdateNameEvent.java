package com.sheepybot.api.entities.event.server.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called whenever a {@link Role}s names is updated (changed)
 */
public class GuildRoleUpdateNameEvent extends GuildRoleEvent {

    private final String oldName;
    private final String newName;

    /**
     * @param guild   The {@link Guild} the {@link Role} belongs too
     * @param role    The {@link Role} affected
     * @param oldName The old names of the {@link Role}
     * @param newName The new names of the {@link Role}
     * @param jda     The {@link JDA} instance
     */
    public GuildRoleUpdateNameEvent(final Guild guild,
                                    final Role role,
                                    final String oldName,
                                    final String newName,
                                    final JDA jda) {
        super(guild, role, jda);
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * @return The old names of the {@link Role}
     */
    public String getOldName() {
        return this.oldName;
    }

    /**
     * @return The new names of the {@link Role}
     */
    public String getNewName() {
        return this.newName;
    }
}
