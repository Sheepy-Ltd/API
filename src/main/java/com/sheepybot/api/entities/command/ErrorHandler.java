package com.sheepybot.api.entities.command;

import com.sheepybot.api.entities.command.argument.ArgumentParser;
import com.sheepybot.api.exception.command.CommandSyntaxException;
import com.sheepybot.api.exception.parser.ParserException;

public interface ErrorHandler {

    /**
     * Handle a {@link Throwable} which occurred during the execution of a command.
     * The {@link Throwable} passed can be one of the following
     * <ul>
     *     <li>{@link CommandSyntaxException} - Improper command usage</li>
     *     <li>{@link ParserException} - A user gave either invalid input or an {@link ArgumentParser} ran into an error</li>
     *     <li>{@link Throwable} - Something else happened that wasn't expected, usually due to improper error handling.</li>
     * </ul>
     *
     * @param throwable The {@link Throwable}
     * @param context   The {@link CommandContext}
     */
    void handle(final Throwable throwable, final CommandContext context);

}
