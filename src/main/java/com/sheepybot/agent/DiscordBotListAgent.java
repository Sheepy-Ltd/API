package com.sheepybot.agent;

import com.sheepybot.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.discordbots.api.client.DiscordBotListAPI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DiscordBotListAgent implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBotListAgent.class);

    private final ShardManager shardManager;
    private final String botId;
    private final DiscordBotListAPI api;

    public DiscordBotListAgent(@NotNull("bot cannot be null") final Bot bot,
                               @NotNull("id cannot be null") final String botId,
                               @NotNull("token cannot be null") final String dblToken) {
        this.shardManager = bot.getShardManager();
        this.botId = botId;
        this.api = new DiscordBotListAPI.Builder()
                .botId(botId)
                .token(dblToken)
                .build();
    }

    @Override
    public void run() {
        LOGGER.info("Submitting bot stats...");
        this.api.setStats((int)this.shardManager.getGuildCache().size())
                .exceptionally(ex -> { LOGGER.error("An erorr occurred and the stats could not be submitted", ex); return null; })
        ;
    }

}
