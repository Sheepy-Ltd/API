package com.sheepybot.internal.listeners;

import com.google.common.collect.Lists;
import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.argument.RawArguments;
import com.sheepybot.api.entities.language.I18n;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GuildMessageListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMessageListener.class);

    private final Bot bot;
    private long commandTimeoutAfter;

    public GuildMessageListener(@NotNull("api cannot be null") final Bot bot) {
        this.bot = bot;
        this.commandTimeoutAfter = bot.getConfig().getLong("client.command_timeout_after", 5_000L);
        if (this.commandTimeoutAfter < 5_000L) {
            LOGGER.warn("Command timeout after cannot be less than 5_000L (5 seconds).");
            this.commandTimeoutAfter = 5_000L;
        }
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {

        final Guild guild = event.getGuild();
        final Member member = event.getMember();
        final Member self = guild.getSelfMember();
        final User user = event.getAuthor();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final JDA jda = event.getJDA();

        final String raw = message.getContentRaw().trim();

        if (member == null || raw.isEmpty() || user.isBot() || message.isWebhookMessage() || !self.hasPermission(channel, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)) {
            return;
        }

        final Future<?> future = Bot.THREAD_POOL.submit(() -> {

            String content = raw;

            final String prefix = Bot.prefixGenerator.apply(guild).toLowerCase();
            final I18n i18n = I18n.getDefaultI18n();

            boolean prefixMention = false;

            if (content.toLowerCase().startsWith(prefix)) {
                content = content.substring(prefix.length());
            } else if (content.startsWith("<@!" + self.getIdLong() + ">") || content.startsWith("<@" + self.getIdLong() + ">")) {
                content = content.replaceFirst("<@!?" + self.getIdLong() + ">", "");
                if (content.trim().isEmpty()) { //@Mention for prefix
                    message.getTextChannel().sendMessage(i18n.tl("commandPrefixMention", prefix, jda.getShardInfo().getShardId())).queue(null, __ -> {
                    });
                } else {
                    prefixMention = true;
                }
            } else {
                Bot.get().getEventRegistry().callEvent(event);
                return;
            }

            if (!content.isEmpty() && (prefixMention || !Character.isWhitespace(content.charAt(0)))) {

                final List<String> split = Lists.newArrayList(content.trim().split("\\s+")); //whitespace

                final String trigger = split.get(0).toLowerCase();
                final Command command = Bot.get().getCommandRegistry().getCommandByNameOrAlias(Collections.singletonList(trigger));

                if (command == null) {
                    Bot.get().getEventRegistry().callEvent(event);
                } else {

                    final List<String> args = split.stream().skip(1).collect(Collectors.toList());

                    final CommandContext context = new CommandContext(channel, member, guild, trigger, command, message, i18n, jda);
                    final RawArguments rawArgs = new RawArguments(args);

                    try {
                        command.handle(context, new Arguments(context, rawArgs));
                    } catch (final Throwable throwable) {
                        this.bot.getAPI().getErrorHandler().handle(throwable, context);
                    }

                }

            }

        });

        //This is just a watchdog, so in the event a command messed up it doesn't slowly start killing the bot
        //Insurance is wonderful when you need it, however unlike most companies this should payout
        Bot.THREAD_POOL.submit(() -> {

            try {
                future.get(this.commandTimeoutAfter, TimeUnit.MILLISECONDS);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

            if (!future.isDone()) {
                future.cancel(true);
            }

        });


    }

}
