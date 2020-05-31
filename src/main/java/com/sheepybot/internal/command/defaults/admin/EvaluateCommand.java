package com.sheepybot.internal.command.defaults.admin;

import com.google.common.base.Stopwatch;
import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.command.parsers.ArgumentParsers;
import com.sheepybot.api.entities.messaging.Messaging;
import com.sheepybot.util.BotUtils;
import okhttp3.Request;
import okhttp3.Response;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class EvaluateCommand implements CommandExecutor {

    /**
     * The script engine we use for eval
     */
    private static final String SCRIPT_ENGINE = "ECMAScript";

    /**
     * How long to wait before we give up and assume the process just ran into some kind of infinite loop
     */
    private static final long EVAL_TIMEOUT_AFTER = 5_000L;

    /**
     * Pastebin pattern so we can use pastebin urls in eval
     */
    private static final Pattern PASTEBIN_PATTERN = Pattern.compile("https://(www.)?pastebin.com/(raw/)?.+");

    /**
     * An array of imports that will probably be used when executing eval
     */
    private static final String[] EVAL_IMPORTS = new String[]{
            "java.lang",
            "java.io",
            "java.math",
            "java.util",
            "java.util.concurrent",
            "java.time",
    };

    private final ScriptEngine scriptEngine;

    public EvaluateCommand() {
        //Change this to graal in the event we ever move to JDK 11+
        this.scriptEngine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE);
        try {
            this.scriptEngine.eval(String.format("var imports = new JavaImporter(%s)", String.join(", ", EVAL_IMPORTS)));
        } catch (final ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {

            this.scriptEngine.put("context", context);
            this.scriptEngine.put("channel", context.getChannel());
            this.scriptEngine.put("member", context.getMember());
            this.scriptEngine.put("user", context.getSender());
            this.scriptEngine.put("guild", context.getGuild());
            this.scriptEngine.put("message", context.getMessage());
            this.scriptEngine.put("bot", context.getGuild().getSelfMember());
            this.scriptEngine.put("jda", context.getJDA());
            this.scriptEngine.put("this", this);

            String input = args.next(ArgumentParsers.REMAINING_STRING_NO_QUOTE);
            if (input.startsWith("```") && input.endsWith("```")) {
                input = input.substring(3, input.length() - 3);
            } else if (this.isPastebinURL(input)) {
                input = this.getPageContent(this.getAsPastebinUrl(input));
            }

            final String finput = input; //I hate lambda sometimes
            final Stopwatch stopwatch = Stopwatch.createUnstarted();

            final Future<?> future = Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {

                String output;
                Color color;
                try {
                    stopwatch.start();

                    final Object result = this.scriptEngine.eval(String.format("with (imports) { %s }", finput));

                    stopwatch.stop();

                    output = result == null ? "null" : result.toString();
                    color = Color.GREEN;

                } catch (final ScriptException ex) {
                    output = ex.getMessage();
                    color = Color.RED;
                }

                Messaging.send(context.getChannel(), Messaging.getLocalEmbedBuilder()
                        .setColor(color)
                        .setTitle("Evaluate")
                        .addField("Input", Messaging.getLocalMessageBuilder().appendCodeBlock(finput, "javascript").build().getContentRaw(), false)
                        .addField("Output", Messaging.getLocalMessageBuilder().appendCodeBlock(output, "javascript").build().getContentRaw(), false)
                        .setFooter(String.format("Completed in %dms", stopwatch.elapsed(TimeUnit.MILLISECONDS)), null).build());

            });

            Bot.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {

                try {
                    future.get(EVAL_TIMEOUT_AFTER, TimeUnit.MILLISECONDS);
                } catch (final InterruptedException | TimeoutException ignored) {
                    context.reply(context.i18n("evalTaskExceededTimeLimit", EVAL_TIMEOUT_AFTER));
                } catch (final Exception ex) {
                    context.reply(context.i18n("evalTaskThrewError", ex.getMessage()));
                }

                if (!future.isDone()) {
                    future.cancel(true);
                }

            });

        }

        return true;
    }

    /**
     * Checks whether the {@code input} matches the {@link #PASTEBIN_PATTERN}
     *
     * @param input The input to match
     * @return {@code true} if the provided input matches {@link #PASTEBIN_PATTERN}
     */
    public boolean isPastebinURL(final String input) {
        return PASTEBIN_PATTERN.matcher(input).matches();
    }

    /**
     * Add the /raw/ to a pastebin url
     *
     * @param url The url
     * @return The raw url
     */
    public String getAsPastebinUrl(final String url) {
        return "https://pastebin.com/raw/" + url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * Get the content of a page.
     *
     * @param url The url
     * @return The page content as a {@link String}
     */
    @SuppressWarnings("ConstantConditions")
    public String getPageContent(final String url) {

        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/javascript")
                .header("Cache-Control", "no-cache")
                .build();

        try (final Response response = Bot.HTTP_CLIENT.newCall(request).execute()) {
            if (response.code() != 200) {
                return null;
            }
            return response.body().string();
        } catch (final IOException ignored) {
        }

        return null;
    }


}
