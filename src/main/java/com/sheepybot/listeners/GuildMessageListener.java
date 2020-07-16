package com.sheepybot.listeners;

import com.google.common.collect.Lists;
import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.argument.RawArguments;
import com.sheepybot.api.entities.language.I18n;
import com.sheepybot.api.entities.messaging.Messaging;
import com.sheepybot.api.exception.command.CommandSyntaxException;
import com.sheepybot.api.exception.parser.ParserException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GuildMessageListener extends ListenerAdapter {

    /**
     * How long commands may run before it's assumed they ran into an improperly handled error / infinite loop
     */
    private static final long COMMAND_TIMEOUT_AFTER = 5_000L;

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

        final Future<?> future = Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {

            String content = raw;

            final String prefix = Bot.get().getConfig().getString("client.prefix");
            final I18n i18n = I18n.getDefaultI18n();

            if (content.startsWith(prefix)) {
                content = content.substring(prefix.length());
            } else if (content.startsWith(guild.getSelfMember().getAsMention())) {
                content = content.substring(guild.getSelfMember().getAsMention().length());
                if (content.trim().isEmpty()) { //@Mention for prefix
                    Messaging.send(channel, i18n.tl("commandPrefixMention", prefix));
                }
            } else {
                Bot.get().getEventRegistry().callEvent(event);
                return;
            }

            content = content.trim();
            if (!content.isEmpty()) {

                final List<String> split = Lists.newArrayList(content.split("\\s+")); //whitespace

                final String trigger = split.get(0).toLowerCase();
                final Command command = Bot.get().getCommandRegistry().getCommandByNameOrAlias(Collections.singletonList(trigger));

                if (command == null) {
                    Bot.get().getEventRegistry().callEvent(event);
                } else {

                    final List<String> args = split.stream().skip(1).collect(Collectors.toList());

                    final CommandContext context = new CommandContext(channel, member, guild, trigger, command, message, i18n, jda);
                    final RawArguments rawArgs = new RawArguments(args);

                    try {
                        command.getExecutor().execute(context, new Arguments(context, rawArgs));
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

        //This is just a watchdog, so in the event a command messed up it doesn't slowly start killing the bot
        //Insurance is wonderful when you need it, however unlike most companies this should payout
        Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {

            try {
                future.get(COMMAND_TIMEOUT_AFTER, TimeUnit.MILLISECONDS);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

            if (!future.isDone()) {
                future.cancel(true);
            }

        });


    }

}
