package com.sheepybot.internal.command.defaults.admin;

import com.google.common.base.Stopwatch;
import com.sheepybot.Bot;
import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.command.parsers.ArgumentParsers;
import com.sheepybot.api.entities.messaging.Messaging;
import com.sheepybot.util.BotUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import okhttp3.Request;
import okhttp3.Response;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class EvaluateCommand implements CommandExecutor {

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
            "net.dv8tion.jda.api",
            "com.sheepybot.api.entities"
    };

    private final Binding binding;
    private final GroovyShell shell;

    public EvaluateCommand() {

        this.binding = new Binding();

        final ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports(EVAL_IMPORTS);

        final CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(imports);

        this.shell = new GroovyShell(this.binding, configuration);
    }

    @Override
    public void execute(final CommandContext context,
                        final Arguments args) {

        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {

            String input = args.next(ArgumentParsers.REMAINING_STRING_NO_QUOTE);
            if (input.startsWith("```") && input.endsWith("```")) {
                input = input.substring(3, input.length() - 3);
            } else if (this.isPastebinURL(input)) {
                input = this.getPageContent(this.getAsPastebinUrl(input));
            }

            this.binding.setVariable("context", context);
            this.binding.setVariable("channel", context.getChannel());
            this.binding.setVariable("member", context.getMember());
            this.binding.setVariable("user", context.getUser());
            this.binding.setVariable("guild", context.getGuild());
            this.binding.setVariable("message", context.getMessage());
            this.binding.setVariable("bot", context.getGuild().getSelfMember());
            this.binding.setVariable("jda", context.getJDA());
            this.binding.setVariable("this", this);

            final String finput = input; //I hate lambda sometimes
            final Stopwatch stopwatch = Stopwatch.createUnstarted();

            final Future<?> future = Bot.THREAD_POOL.submit(() -> {

                String output;
                Color color;
                try {
                    stopwatch.start();

                    final Object result = this.shell.evaluate(String.format("%s", finput));

                    stopwatch.stop();

                    output = result == null ? "null" : result.toString();
                    color = Color.GREEN;

                } catch (final CompilationFailedException ex) {
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

            Bot.THREAD_POOL.submit(() -> {

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
