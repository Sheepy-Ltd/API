package com.sheepybot.api.entities.event.guild.member.voice;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

public class MemberVoiceLeaveEvent extends MemberVoiceEvent {

    /**
     * @param guild   The {@link Guild} this event was triggered in
     * @param channel The {@link VoiceChannel} left
     * @param member  The {@link Member} that triggered this event
     * @param jda     The {@link JDA} instance
     */
    public MemberVoiceLeaveEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                 @NotNull(value = "channel cannot be null") final VoiceChannel channel,
                                 @NotNull(value = "member cannot be null") final Member member,
                                 @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, member, jda);
    }

}
