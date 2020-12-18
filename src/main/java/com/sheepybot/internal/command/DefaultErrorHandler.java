package com.sheepybot.internal.command;

import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.ErrorHandler;
import com.sheepybot.api.exception.command.CommandSyntaxException;
import com.sheepybot.api.exception.parser.ParserException;

public class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void handle(final Throwable throwable,
                       final CommandContext context) {

        if (throwable instanceof CommandSyntaxException) {
            context.reply(context.i18n("commandCorrectUsage", context.getCommand().getName(), context.getCommand().getUsage()));
        } else if (throwable instanceof ParserException) {
            context.reply(throwable.getMessage());
        } else {
            context.reply(context.i18n("commandUncaughtError", throwable.getMessage()));
            throwable.printStackTrace();
        }

    }

}
