package com.sheepybot.api.entities.event.server.member.message;

import com.sheepybot.api.entities.event.server.TextChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

//maybe find a better event to base this off
public class MessageBulkDeleteEvent extends TextChannelEvent {

    private final List<String> messageIds;

    /**
     * @param guild      The {@link Guild} this event was triggered in
     * @param channel    The {@link TextChannel} this event was triggered in
     * @param messageIds A {@link java.util.Collection} of message ids
     * @param jda        The {@link JDA} instance
     */
    public MessageBulkDeleteEvent(@NotNull(value = "guild cannot be null") final Guild guild,
                                  @NotNull(value = "channel cannot be null") final TextChannel channel,
                                  @NotNull(value = "message ids cannot be null") final List<String> messageIds,
                                  @NotNull(value = "jda cannot be null") final JDA jda) {
        super(guild, channel, null, null, -1, jda);
        this.messageIds = messageIds;
    }

    /**
     * Returns a {@link Collection} containing the id of every message
     * deleted in this bulk deletion
     *
     * @return A {@link Collection} of message ids
     */
    public List<String> getMessageIds() {
        return this.messageIds;
    }

}
