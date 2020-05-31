package com.sheepybot.api.entities.event.guild;

import com.sheepybot.api.entities.event.HandlerList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class VoiceChannelEvent extends GuildEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final VoiceChannel channel;

    /**
     * @param channel The {@link TextChannel} this event was triggered in
     * @param server  The {@link Guild} this event was triggered in
     * @param jda     The {@link JDA} instance
     */
    public VoiceChannelEvent(@NotNull(value = "server cannot be null") final Guild server,
                             @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                             @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, jda);
        this.channel = channel;
    }

    /**
     * @return The {@link VoiceChannel} this event was triggered in
     */
    public VoiceChannel getChannel() {
        return this.channel;
    }

//    @Override
//    public HandlerList getHandlerList() {
//        return HANDLER_LIST;
//    }

}
