package com.sheepybot.api.entities.event.guild.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class MemberVoiceMoveEvent extends MemberVoiceEvent {

    private final VoiceChannel channelLeft;

    /**
     * @param server        The {@link Guild} this event was triggered in
     * @param member        The {@link Member} that triggered this event
     * @param channelJoined The {@link VoiceChannel} joined
     * @param channelLeft   The {@link VoiceChannel} left
     * @param jda           The {@link JDA} instance
     */
    public MemberVoiceMoveEvent(@NotNull(value = "guild cannot be null") final Guild server,
                                @NotNull(value = "member cannot be null") final Member member,
                                @NotNull(value = "joined cannot be null") final VoiceChannel channelJoined,
                                @NotNull(value = "left cannot be null") final VoiceChannel channelLeft,
                                @NotNull(value = "jda cannot be null") final JDA jda) {
        super(server, channelJoined, member, jda);
        this.channelLeft = channelLeft;
    }

    /**
     * @return The {@link VoiceChannel} the {@link Member} was moved from
     */
    public VoiceChannel getChannelLeft() {
        return this.channelLeft;
    }

}
