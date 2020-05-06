package com.sheepybot.api.event.server.member.message;

import com.sheepybot.api.event.server.TextChannelEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class MessageBulkDeleteEvent extends TextChannelEvent<TextChannel> {

    private final List<String> messageIds;

    public MessageBulkDeleteEvent(final Guild server,
                                  final TextChannel channel,
                                  final List<String> messageIds,
                                  final JDA jda) {
        super(server, channel, jda);
        this.messageIds = messageIds;
    }

    /**
     * Returns a list containing the id of every message
     * deleted in this bulk deletion
     *
     * @return A list of message ids
     */
    public List<String> getMessageIds() {
        return this.messageIds;
    }

}
