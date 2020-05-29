package com.sheepybot.api.entities.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class GuildLeaveEvent extends GuildEvent {

    /**
     * @param server The {@link Guild} left
     */
    public GuildLeaveEvent(@NotNull(value = "guild cannot be null") final Guild server,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, jda);
    }

}
