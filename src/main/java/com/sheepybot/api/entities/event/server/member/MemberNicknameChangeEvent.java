package com.sheepybot.api.entities.event.server.member;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class MemberNicknameChangeEvent extends MemberEvent {

    private final String oldNickname;
    private final String newNickname;

    /**
     * @param guild       The {@link Guild} the event was triggered in
     * @param member      The {@link Member} that triggered this event
     * @param oldNickname The old nickname
     * @param newNickname The new nickname
     * @param jda         The {@link JDA} instance
     */
    public MemberNicknameChangeEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                     @NotNull(value = "member cannot be null") final Member member,
                                     final String oldNickname,
                                     final String newNickname,
                                     @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, member, member.getUser(), member.getUser().getIdLong(), jda);
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    /**
     * @return The possibly null old nickname
     */
    public String getOldNickname() {
        return this.oldNickname;
    }

    /**
     * @return The possibly null new nickname
     */
    public String getNewNickname() {
        return this.newNickname;
    }
}
