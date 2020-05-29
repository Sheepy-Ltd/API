package com.sheepybot.api.entities.event.server.member;

import com.sheepybot.api.entities.event.server.GuildEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class MemberEvent extends GuildEvent {

    private final Member member;
    private final User user;
    private final long userId;
    private final JDA jda;

    /**
     * @param guild  The {@link Guild} this event was triggered in
     * @param member The {@link Member} who triggered this event
     * @param userId The {@link User} ID who triggered this event
     * @param jda    The {@link JDA} instance
     */
    public MemberEvent(@NotNull(value = "server cannot be null") final Guild guild,
                       final Member member,
                       final User user,
                       final long userId,
                       @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, jda);
        this.member = member;
        this.user = user;
        this.userId = userId;
        this.jda = jda;
    }

    /**
     * @return The {@link User} ID responsible for this event
     */
    public long getUserId() {
        return this.userId;
    }

    /**
     * @return The possibly null {@link User} responsible for this event
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @return The possibly null {@link Member} responsible for this event
     */
    public Member getMember() {
        return this.member;
    }

}
