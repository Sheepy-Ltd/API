package com.sheepybot.listeners;

import com.sheepybot.Bot;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.command.RootCommandRegistry;
import com.sheepybot.api.entities.event.RootEventRegistry;

public class BotListener extends ListenerAdapter {

    private final Bot bot;

    public BotListener(@NotNull(value = "bot cannot be null") final Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public RootCommandRegistry getCommandManager() {
        return this.bot.getCommandRegistry();
    }

    public RootEventRegistry getEventManager() {
        return this.bot.getEventRegistry();
    }

//    public MongoDatabase getMongoDatabase() {
//        return this.bot.getMongoDatabase();
//    }
//
//    public WebhookClient getWebhookClient() {
//        return this.bot.getWebhookClient();
//    }

}
