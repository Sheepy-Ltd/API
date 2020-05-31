package com.sheepybot.api.entities.event.guild.role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;

/**
 * Called when a {@link Role}s color is updated
 */
public class GuildRoleUpdateColorEvent extends GuildRoleEvent {

    private final Color oldColor;
    private final Color newColor;

    /**
     * @param guild    The {@link Guild} the {@link Role} belongs too
     * @param role     The {@link Role} affected by this action
     * @param oldColor The old {@link Color} of the {@link Role}
     * @param newColor The new {@link Color} of the {@link Role}
     * @param jda      The {@link JDA} instance
     */
    public GuildRoleUpdateColorEvent(final Guild guild,
                                     final Role role,
                                     final Color oldColor,
                                     final Color newColor,
                                     final JDA jda) {
        super(guild, role, jda);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    /**
     * @return The old {@link Role} color
     */
    public Color getOldColor() {
        return this.oldColor;
    }

    /**
     * @return The new {@link Role} color
     */
    public Color getNewColor() {
        return this.newColor;
    }
}
