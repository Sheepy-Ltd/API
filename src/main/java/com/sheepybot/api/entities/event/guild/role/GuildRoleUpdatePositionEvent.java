package com.sheepybot.api.entities.event.guild.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

/**
 * Called whenever a {@link Role}s position is changed
 */
public class GuildRoleUpdatePositionEvent extends GuildRoleEvent {

    private final int oldPosition;
    private final int newPosition;

    /**
     * @param guild       The {@link Guild} the {@link Role} belongs too
     * @param role        The {@link Role} affected
     * @param oldPosition The old position of the {@link Role}
     * @param newPosition The new position of the {@link Role}
     * @param jda         The {@link JDA} instance
     */
    public GuildRoleUpdatePositionEvent(final Guild guild,
                                        final Role role,
                                        final int oldPosition,
                                        final int newPosition,
                                        final JDA jda) {
        super(guild, role, jda);
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    /**
     * @return The old position of the {@link Role}
     */
    public int getOldPosition() {
        return this.oldPosition;
    }

    /**
     * @return The new position of the {@link Role}
     */
    public int getNewPosition() {
        return this.newPosition;
    }
}
