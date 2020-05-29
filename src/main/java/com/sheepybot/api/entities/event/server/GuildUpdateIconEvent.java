package com.sheepybot.api.entities.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Guild} changes its names
 */
public class GuildUpdateIconEvent extends GuildEvent {

    private final String oldIcon;
    private final String newIcon;

    /**
     * @param oldIcon The old icon
     * @param newIcon The new icon
     * @param server  The {@link Guild} that triggered this event
     * @param jda     The {@link JDA} instance
     */
    public GuildUpdateIconEvent(final String oldIcon,
                                final String newIcon,
                                @NotNull(value = "server cannot be null") final Guild server,
                                @NotNull(value = "jda instance cannot be null") final JDA jda) {
        super(server, jda);
        this.oldIcon = oldIcon;
        this.newIcon = newIcon;
    }

    /**
     * @return The possibly null old icon
     */
    public String getOldIcon() {
        return this.oldIcon;
    }

    /**
     * @return The possibly null new icon
     */
    public String getNewIcon() {
        return this.newIcon;
    }
}
