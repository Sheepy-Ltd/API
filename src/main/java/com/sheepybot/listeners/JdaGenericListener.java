package com.sheepybot.listeners;

import com.sheepybot.Bot;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JdaGenericListener extends ListenerAdapter {

    @Override
    public void onGenericEvent(final GenericEvent event) {
        //we ignore guild message received event as thats fired internally and not through JDA
        if (event instanceof Event && !(event instanceof GuildMessageReceivedEvent)) {
            Bot.get().getEventRegistry().callEvent((Event) event);
        }
    }

}
