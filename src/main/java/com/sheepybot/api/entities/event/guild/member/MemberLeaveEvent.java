package com.sheepybot.api.entities.event.guild.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class MemberLeaveEvent extends MemberEvent {

    /**
     * @param guild The {@link Guild} this event was triggered in
     * @param user  The {@link User} that left
     * @param jda   The {@link JDA} instance
     */
    public MemberLeaveEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                            @NotNull(value = "member cannot be null") final User user,
                            @NotNull(value = "member cannot be null") final JDA jda) {
        super(guild, null, user, user.getIdLong(), jda);
    }

}
