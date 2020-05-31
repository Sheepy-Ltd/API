package com.sheepybot.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.sheepybot.Bot;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
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
    public static boolean isBotAdmin(@NotNull(value = "member cannot be null") final Member member) {
        return isBotAdmin(member.getUser());
    }

    /**
     * Check whether a {@link Member} is a bot admin
     *
     * @param user The {@link User}
     *
     * @return {@code true} if the provided {@link Member} is an administrator, {@code false} otherwise
     */
    public static boolean isBotAdmin(@NotNull(value = "user cannot be null") final User user) {
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
    public static BufferedImage getImageFromURL(final String url) throws IOException {

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

    //courtesy of https://github.com/essentials/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/DateUtil.java
    //whilst I can do the method, I couldn't remember the regex

    /**
     * @param time   The time
     * @param future Whether this is set in the future or in the past
     * @return The time
     * @throws Exception If the input {@code time} isn't parsable
     */
    public static long getDateFromInputTime(final String time,
                                            final boolean future) throws Exception {

        final Matcher matcher = TIME_PATTERN.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        boolean found = false;

        while (matcher.find()) {

            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }

            for (int i = 0; i < matcher.groupCount(); i++) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }

            if (found) {

                if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                    years = Integer.parseInt(matcher.group(1));
                }

                if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                    months = Integer.parseInt(matcher.group(2));
                }

                if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                    weeks = Integer.parseInt(matcher.group(3));
                }

                if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                    days = Integer.parseInt(matcher.group(4));
                }

                if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                    hours = Integer.parseInt(matcher.group(5));
                }

                if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                    minutes = Integer.parseInt(matcher.group(6));
                }

                if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {
                    seconds = Integer.parseInt(matcher.group(7));
                }

                break;
            }
        }

        if (!found) {
            throw new Exception("Invalid date format");
        }

        Calendar c = new GregorianCalendar();

        if (years > 0) {
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        }

        if (months > 0) {
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        }

        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        }

        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        }

        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }

        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        }

        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        }

        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, 10);

        if (c.after(calendar)) {
            return calendar.getTimeInMillis();
        }

        return c.getTimeInMillis();
    }

    /**
     * Get the recommended shard count using the Bot token from Discords /gateway/bot endpoint
     *
     * @param token The bot token
     * @return The recommended shard count or -1 if an I/O error occurred
     */
    public static int getRecommendedShards(@NotNull(value = "token cannot be null") final String token) {

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


    /**
     * Retrieve an activity type from the input {@code activityType}, should no
     * valid {@link Activity.ActivityType} be specified the default return value is
     * {@link Activity.ActivityType#DEFAULT}
     *
     * @param activityType The activity type
     * @return The {@link Activity.ActivityType}
     */
    public static Activity.ActivityType getActivityTypeFromString(final String activityType) {
        switch (activityType.toLowerCase()) {
            case "streaming":
                return Activity.ActivityType.STREAMING;
            case "listening":
                return Activity.ActivityType.LISTENING;
            default:
                return Activity.ActivityType.DEFAULT;
        }
    }

    /**
     * Get a Bot user ID from a token using Discords /users/@me endpoint
     *
     * @param token The bot token
     * @return The bot user ID
     * @throws RuntimeException If we couldn't retrieve the self user ID
     */
    public static long getUserIdFromToken(@NotNull(value = "token cannot be null") final String token) {

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

