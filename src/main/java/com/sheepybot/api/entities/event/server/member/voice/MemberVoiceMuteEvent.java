package com.sheepybot.api.entities.event.server.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Called whenever a {@link Member} toggles their muted state
 */
public class MemberVoiceMuteEvent extends MemberVoiceEvent {

    /**
     * @param guild   The {@link Guild} this event was triggered in
     * @param channel The {@link VoiceChannel} joined
     * @param member  The {@link Member} that triggered this event
     * @param jda     The {@link JDA} instance
     */
    public MemberVoiceMuteEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                final VoiceChannel channel,
                                @NotNull(value = "member cannot be null") final Member member,
                                @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, jda);
    }

}
