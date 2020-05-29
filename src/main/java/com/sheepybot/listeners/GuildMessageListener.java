package com.sheepybot.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sheepybot.Bot;
import com.sheepybot.Environment;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.argument.RawArguments;
import com.sheepybot.api.entities.configuration.GuildSettings;
import com.sheepybot.api.entities.event.server.member.MemberChatEvent;
import com.sheepybot.api.entities.language.I18n;
import com.sheepybot.api.entities.messaging.Messaging;
import com.sheepybot.api.entities.utils.BotUtils;
import com.sheepybot.api.exception.command.CommandSyntaxException;
import com.sheepybot.api.exception.parser.ParserException;
import com.sheepybot.util.RateLimiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildMessageListener extends ListenerAdapter {

    /**
     * The {@link Pattern} used to match a given string as a flag
     */
    private static final Pattern FLAG_PATTERN = Pattern.compile("--([\\w-]+)(?:\\s+(\\w+))?");

    /**
     * The maximum number of commands executable within a given period
     */
    private static final int COMMAND_REQUEST_LIMIT = 10;

    /**
     * How many seconds to wait before a user can execute another {@link #COMMAND_REQUEST_LIMIT} commands
     */
    private static final int COMMAND_REQUEST_RATE_LIMIT_REFRESH_AFTER = 15;

    /**
     * How long commands may run before it's assumed they ran into an improperly handled error / infinite loop
     */
    private static final long COMMAND_TIMEOUT_AFTER = 5_000L;


    /**
     * A cache of rate limits from a member
     */
    private final Map<Long, RateLimiter> rateLimits;

    public GuildMessageListener() {
        this.rateLimits = Maps.newHashMap();
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {

        final Member member = event.getMember();
        final User user = event.getAuthor();
        final Guild guild = event.getGuild();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final JDA jda = event.getJDA();

        final String raw = message.getContentRaw().trim();

        if (member == null || raw.isEmpty() || user.isBot() || message.isWebhookMessage() || !PermissionUtil.checkPermission(channel, guild.getSelfMember(), Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_EXT_EMOJI)) {
            return;
        }

        final Future<?> future = Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> { //this is done async so as we get bigger we don't end up with bigger delays on commands

            final GuildSettings config = GuildSettings.getSettings(guild);
            final I18n i18n = I18n.getI18n(guild);

            //get the prefix, if this is a dev environment then the prefix is hard coded

            final String prefix = Environment.getEnvironment().isRelease() ? config.getString("prefix") : Environment.getEnvironment().getPrefix(); //prefix shouldn't return null but if it does /shrug

            String content = raw;

            //check if this is a command, if it isn't then call the chat event
            if (content.startsWith(prefix)) {
                content = content.substring(prefix.length());
            } else if (content.startsWith(guild.getSelfMember().getAsMention())) {
                content = content.substring(guild.getSelfMember().getAsMention().length());
                if (content.trim().isEmpty()) { //@Mention for prefix
                    Messaging.send(channel, i18n.tl("commandPrefixMention", prefix));
                }
            } else {
                Bot.get().getEventRegistry().callEvent(new MemberChatEvent(guild, channel, member, message, jda));
                return;
            }

            content = content.trim();
            if (!content.isEmpty()) {

                final String[] split = content.split("\\s+"); //whitespace

                final String trigger = split[0].toLowerCase();
                final Command command = Bot.get().getCommandRegistry().getCommandByNameOrAlias(trigger);

                //we rate limit commands so that people can't get the bot rate limited HA, well at least we try to anyway
                final RateLimiter limiter = this.rateLimits.computeIfAbsent(user.getIdLong(), id -> new RateLimiter(COMMAND_REQUEST_LIMIT,
                        COMMAND_REQUEST_RATE_LIMIT_REFRESH_AFTER,
                        TimeUnit.SECONDS));
                if (command == null || !BotUtils.isBotAdmin(user) && !limiter.tryAcquire()) {
                    Bot.get().getEventRegistry().callEvent(new MemberChatEvent(guild, channel, member, message, jda));
                } else {

                    final Matcher matcher = FLAG_PATTERN.matcher(content);

                    final String[] args = this.getArgs(split);
                    final List<Command.Flag> flags = this.getFlags(matcher);

                    final CommandContext context = new CommandContext(channel, member, guild, config, trigger, command, message, i18n, jda);
                    final RawArguments rawArgs = new RawArguments(args);

                    try {
                        command.getExecutor().execute(context, new Arguments(context, rawArgs, flags));
                    } catch (final CommandSyntaxException ex) {
                        context.reply(context.i18n("commandCorrectUsage", ex.getCommand(), ex.getSyntax()));
                    } catch (final ParserException ex) {
                        context.reply(ex.getMessage());
                    } catch (final Exception ex) {
                        context.reply(context.i18n("commandUncaughtError", ex.getMessage()));
                        ex.printStackTrace();
                    }

                }

            }

        });

        //This is just a watchdog, so in the event I messed up a command doesn't slowly start killing the bot
        //Insurance is wonderful when you need it, however unlike most companies this should payout
        Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {

            try {
                future.get(COMMAND_TIMEOUT_AFTER, TimeUnit.MILLISECONDS);
            } catch (final Exception ex) {
                message.getChannel().sendMessage(String.format("An error occurred whilst attempting to execute that command, if this" +
                        "persists please contact the developers: %s", ex.getMessage())).queue();
            }

            if (!future.isDone()) {
                future.cancel(true);
            }

        });


    }

    /**
     * Iterates over the input {@code args} until the {@link #FLAG_PATTERN} regex
     * matches an input argument
     *
     * @param args The args to retrieve
     * @return A list of arguments
     */
    private String[] getArgs(final String[] args) {

        final List<String> newArgs = Lists.newArrayList();

        for (int i = 1; i < args.length; i++) {

            final String argument = args[i];
            if (FLAG_PATTERN.matcher(argument).matches()) {
                break;
            }

            newArgs.add(argument);

        }

        return newArgs.toArray(new String[0]);

    }

    /**
     * @param matcher The {@link Matcher}
     * @return A list of {@link Command.Flag}s, this list may be empty but will never return {@code null}.
     */
    private List<Command.Flag> getFlags(final Matcher matcher) {

        final List<Command.Flag> matches = Lists.newArrayList();
        while (matcher.find()) {

            final String flag = matcher.group(1);

            String value = null;
            if (matcher.groupCount() > 1) { //allows for people putting an = sign for a flag
                value = matcher.group(2);
            }

            matches.add(new Command.Flag(flag, value));

        }

        return matches;
    }

}
