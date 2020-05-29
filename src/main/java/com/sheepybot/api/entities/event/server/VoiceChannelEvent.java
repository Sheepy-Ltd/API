package com.sheepybot.api.entities.event.server;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class VoiceChannelEvent<T extends VoiceChannel> extends GuildEvent {

    private final T channel;

    /**
     * @param channel The {@link TextChannel} this event was triggered in
     * @param server  The {@link Guild} this event was triggered in
     * @param jda     The {@link JDA} instance
     */
    public VoiceChannelEvent(@NotNull(value = "server cannot be null") final Guild server,
                             @NotNull(value = "channel cannot be null") final T channel,
                             @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, jda);
        this.channel = channel;
    }

    /**
     * @return The {@link net.dv8tion.jda.api.entities.Invite.Channel} this event was triggered in
     */
    public T getChannel() {
        return this.channel;
    }

    /**
     * Checks the {@link ChannelType}
     *
     * @param type The {@link ChannelType}
     *
     * @return {@code true}, {@code false} otherwise
     */
    public boolean isFromType(final ChannelType type) {
        return this.channel.getType() == type;
    }

}
