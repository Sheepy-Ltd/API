package com.sheepybot.api.entities.event.server.member.voice;

import com.sheepybot.api.entities.event.server.voice.VoiceEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class MemberVoiceEvent extends VoiceEvent {

    private final Member member;

    /**
     * @param guild   The {@link Guild} that this event was triggered in
     * @param channel The {@link VoiceChannel} this event was triggered in
     * @param member  The {@link Member} who triggered this event
     * @param jda     The {@link JDA} instance
     */
    public MemberVoiceEvent(@NotNull(value = "server cannot be null") final Guild guild,
                            @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                            @NotNull(value = "member cannot be null") final Member member,
                            @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, jda);
        this.member = member;
    }

    /**
     * @return The {@link Member} that triggered this event
     */
    public Member getMember() {
        return this.member;
    }
}
