package com.sheepybot.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.sheepybot.Bot;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BotUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotUtils.class);

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
            "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

    /**
     * Check whether a {@link Member} is a bot admin
     *
     * @param member The {@link Member}
     * @return {@code true} if the provided {@link Member} is an administrator, {@code false} otherwise
     */
    public static boolean isBotAdmin(@NotNull("member cannot be null") final Member member) {
        return isBotAdmin(member.getUser());
    }

    /**
     * Check whether a {@link Member} is a bot admin
     *
     * @param user The {@link User}
     * @return {@code true} if the provided {@link Member} is an administrator, {@code false} otherwise
     */
    public static boolean isBotAdmin(@NotNull("user cannot be null") final User user) {
        for (final long id : Bot.get().getConfig().<Long>getList("client.bot_admins", Lists.newArrayListWithCapacity(0))) {
            if (id == user.getIdLong()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve an image from a url
     *
     * @param url The destination url
     * @return A {@link BufferedImage}
     * @throws IOException If an I/O error occurred
     */
    public static BufferedImage getImageFromURL(@NotNull("url cannot be null") final String url) throws IOException {

        final Request request = new Request.Builder()
                .url(url)
                .build();

        final Response response = Bot.HTTP_CLIENT.newCall(request).execute();
        if (response.body() == null) {
            return null;
        }

        final BufferedImage result = ImageIO.read(response.body().byteStream());

        response.close();

        return result;
    }

    /**
     * Get the recommended shard count using the Bot token from Discords /gateway/bot endpoint
     *
     * @param token The bot token
     * @return The recommended shard count or -1 if an I/O error occurred
     */
    public static int getRecommendedShards(@NotNull("token cannot be null") final String token) {

        final Request request = new Request.Builder()
                .get()
                .url(Requester.DISCORD_API_PREFIX + "/gateway/bot")
                .addHeader("Authorization", "Bot " + token)
                .addHeader("User-Agent", Bot.USER_AGENT)
                .build();

        try {

            int shards = -1;
            int attempts = 0;

            Response response = null;

            while (shards == -1 && attempts++ < 5) {
                response = Bot.HTTP_CLIENT.newCall(request).execute();
                if (response.code() == 200) {
                    final JsonObject object = Bot.JSON_PARSER.parse(response.body().string()).getAsJsonObject();
                    shards = object.get("shards").getAsInt();
                } else {
                    LOGGER.info(String.format("Couldn't retrieve recommended shard count, trying again (%d attempt(s) remaining)", attempts));
                    Thread.sleep(5_000);
                }
            }

            response.close();

            return shards;
        } catch (final IOException | InterruptedException ex) {
            LOGGER.info("An error occurred whilst attempting to retrieve recommended shard count", ex);
        }

        return -1;
    }

    public static OnlineStatus getOnlineStatusFromString(@NotNull("activity type cannot be null") final String onlineStatus) {
        switch (onlineStatus.toLowerCase()) {
            case "idle":
                return OnlineStatus.IDLE;
            case "dnd":
                return OnlineStatus.DO_NOT_DISTURB;
            case "invisible":
            case "offline":
                return OnlineStatus.OFFLINE;
            default:
                return OnlineStatus.ONLINE;
        }
    }

    /**
     * Retrieve an activity type from the input {@code activityType}, should no
     * valid {@link Activity.ActivityType} be specified the default return value is
     * {@link Activity.ActivityType#DEFAULT}
     *
     * @param activityType The activity type
     * @return The {@link Activity.ActivityType}
     */
    public static Activity.ActivityType getActivityTypeFromString(@NotNull("activity type cannot be null") final String activityType) {
        switch (activityType.toLowerCase()) {
            case "streaming":
                return Activity.ActivityType.STREAMING;
            case "listening":
                return Activity.ActivityType.LISTENING;
            default:
                return Activity.ActivityType.DEFAULT;
        }
    }

    public static List<GatewayIntent> getGatewayIntentsFromList(@NotNull("intents cannot be null") final List<String> intents) {
        final List<GatewayIntent> gatewayIntents = new ArrayList<>();

        for (final String intent : intents) {
            final GatewayIntent gatewayIntent = BotUtils.getGatewayIntentFromString(intent);
            if (gatewayIntent == null) {
                LOGGER.info(String.format("Couldn't retrieve gateway intent %s", intent));
            } else {
                gatewayIntents.add(gatewayIntent);
            }
        }

        return gatewayIntents;
    }

    public static GatewayIntent getGatewayIntentFromString(@NotNull("gateway intent cannot be null") final String gatewayIntent) {
        for (final GatewayIntent intent : GatewayIntent.values()) {
            if (intent.name().equalsIgnoreCase(gatewayIntent)) {
                return intent;
            }
        }
        return null;
    }

    public static List<CacheFlag> getCacheFlagsFromList(@NotNull("cache flags cannot be null") final List<String> cacheFlags) {
        final List<CacheFlag> flags = new ArrayList<>();

        for (final String flag : cacheFlags) {
            final CacheFlag cacheFlag = BotUtils.getCacheFlagFromString(flag);
            if (cacheFlag == null) {
                LOGGER.info(String.format("Couldn't retrieve cache flag %s", flag));
            } else {
                flags.add(cacheFlag);
            }
        }

        return flags;
    }

    public static CacheFlag getCacheFlagFromString(@NotNull("cache flag cannot be null") final String cacheFlag) {
        for (final CacheFlag flag : CacheFlag.values()) {
            if (flag.name().equalsIgnoreCase(cacheFlag)) {
                return flag;
            }
        }
        return null;
    }

    public static MemberCachePolicy getMemberCachePolicyFromString(@NotNull("cache policy cannot be null") final String cachePolicy) {
        switch (cachePolicy.toLowerCase()) {
            case "all":
                return MemberCachePolicy.ALL;
            case "owner":
                return MemberCachePolicy.OWNER;
            case "online":
                return MemberCachePolicy.ONLINE;
            case "voice":
                return MemberCachePolicy.VOICE;
            case "none":
                return MemberCachePolicy.NONE;
            default:
                return MemberCachePolicy.DEFAULT;
        }
    }

    public static ChunkingFilter getChunkingFilterFromString(@NotNull("chunking filter cannot be null") final String chunkingFilter) {
        if (chunkingFilter.equalsIgnoreCase("all")) {
            return ChunkingFilter.ALL;
        }
        return ChunkingFilter.NONE;
    }

    /**
     * Get a Bot user ID from a token using Discords /users/@me endpoint
     *
     * @param token The bot token
     * @return The bot user ID
     * @throws RuntimeException If we couldn't retrieve the self user ID
     */
    public static long getUserIdFromToken(@NotNull("token cannot be null") final String token) {

        final Request request = new Request.Builder()
                .get()
                .url(Requester.DISCORD_API_PREFIX + "/users/@me")
                .addHeader("Authorization", "Bot " + token)
                .addHeader("User-Agent", Bot.USER_AGENT)
                .build();

        try {

            String result = "";
            int attempts = 0;

            Response response = null;

            while (result.isEmpty() && attempts++ < 5) {
                response = Bot.HTTP_CLIENT.newCall(request).execute();
                if (response.code() == 200) {
                    final JsonObject object = Bot.JSON_PARSER.parse(response.body().string()).getAsJsonObject();
                    result = object.get("id").getAsString();
                } else {
                    LOGGER.info(String.format("Couldn't retrieve user ID, trying again (%d attempt(s) remaining)", attempts));
                    Thread.sleep(5_000);
                }
            }

            response.close();

            if (result.isEmpty()) {
                throw new RuntimeException("Couldn't retrieve self user ID after 5 attempt(s)");
            }

            long botId;
            try {
                botId = Long.parseLong(result);
            } catch (final NumberFormatException ex) {
                throw new RuntimeException("Retrieved self user ID from discord but couldn't parse the result", ex);
            }

            return botId;
        } catch (final IOException | InterruptedException ex) {
            LOGGER.info("An error occurred whilst attempting to retrieve self user ID", ex);
        }

        return -1;
    }

}

