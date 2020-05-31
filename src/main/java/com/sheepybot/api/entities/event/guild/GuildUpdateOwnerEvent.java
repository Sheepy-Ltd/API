package com.sheepybot.api.entities.event.guild;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Guild} changes its names
 */
public class GuildUpdateOwnerEvent extends GuildEvent {

    private final Member oldOwner;
    private final Member newOwner;

    /**
     * @param oldOwner The old owner
     * @param newOwner The new owner
     * @param server   The {@link Guild} that triggered this event
     * @param jda      The {@link JDA} instance
     */
    public GuildUpdateOwnerEvent(final Member oldOwner,
                                 final Member newOwner,
                                 @NotNull(value = "server cannot be null") final Guild server,
                                 @NotNull(value = "jda instance cannot be null") final JDA jda) {
        super(server, jda);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    /**
     * @return The possibly null old owner
     */
    public Member getOldOwner() {
        return this.oldOwner;
    }

    /**
     * @return The possibly null new owner
     */
    public Member getNewOwner() {
        return this.newOwner;
    }
}
