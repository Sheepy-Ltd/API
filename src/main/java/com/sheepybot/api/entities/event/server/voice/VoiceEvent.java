package com.sheepybot.api.entities.event.server.voice;

import com.sheepybot.api.entities.event.server.VoiceChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class VoiceEvent extends VoiceChannelEvent<VoiceChannel> {

    /**
     * @param guild   The {@link Guild} this event was triggered in
     * @param channel The {@link VoiceChannel} affected
     * @param jda     The {@link JDA} instance
     */
    public VoiceEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                      @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                      @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, jda);
    }

}
