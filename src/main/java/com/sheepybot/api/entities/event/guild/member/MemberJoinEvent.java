package com.sheepybot.api.entities.event.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class MemberJoinEvent extends MemberEvent {

    /**
     * @param guild  The {@link Guild} this event was triggered in
     * @param member The {@link Member} that joined
     * @param jda    The {@link JDA} instance
     */
    public MemberJoinEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                           @NotNull(value = "member cannot be null") final Member member,
                           @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, member, member.getUser(), member.getUser().getIdLong(), jda);
    }

}
