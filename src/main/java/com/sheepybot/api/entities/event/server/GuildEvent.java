package com.sheepybot.api.entities.event.server;

import com.sheepybot.api.entities.event.Event;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

public class GuildEvent extends Event {

    private final Guild guild;
    private final JDA jda;

    /**
     * @param guild The {@link Guild} this event was triggered in
     * @param jda   The {@link JDA} instance
     */
    public GuildEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                      @NotNull(value = "guild cannot be null") final JDA jda) {
        super(true);
        this.guild = guild;
        this.jda = jda;
    }

    /**
     * @return The {@link Guild} this event was triggered in
     */
    public Guild getGuild() {
        return this.guild;
    }

    /**
     * @return The {@link JDA} instance
     */
    public JDA getJDA() {
        return this.jda;
    }

}
