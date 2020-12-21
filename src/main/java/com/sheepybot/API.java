package com.sheepybot;

import com.sheepybot.api.entities.command.CommandHandler;
import com.sheepybot.api.entities.command.ErrorHandler;
import com.sheepybot.api.entities.command.UsageFormatter;
import com.sheepybot.internal.command.DefaultCommandHandler;
import com.sheepybot.internal.command.DefaultErrorHandler;
import com.sheepybot.internal.command.DefaultUsageFormatter;
import org.jetbrains.annotations.NotNull;

public class API {

    public static final CommandHandler DEFAULT_COMMAND_HANDLER = new DefaultCommandHandler();
    public static final ErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
    public static final UsageFormatter DEFAULT_USAGE_FORMATTER = new DefaultUsageFormatter();

    private CommandHandler commandHandler;
    private ErrorHandler errorHandler;
    private UsageFormatter usageFormatter;

    API() {
        this.commandHandler = DEFAULT_COMMAND_HANDLER;
        this.errorHandler = DEFAULT_ERROR_HANDLER;
        this.usageFormatter = DEFAULT_USAGE_FORMATTER;
    }

    /**
     * @return The {@link CommandHandler} to use.
     */
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    /**
     * @param commandHandler The {@link CommandHandler}.
     */
    public void setCommandHandler(@NotNull("command handler cannot be null") final CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * @return The {@link ErrorHandler} to use.
     */
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    /**
     * @param errorHandler The {@link ErrorHandler}.
     */
    public void setErrorHandler(@NotNull("error handler cannot be null") final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * @return The {@link UsageFormatter}
     */
    public UsageFormatter getUsageFormatter() {
        return this.usageFormatter;
    }

    /**
     * Set a {@link UsageFormatter}, useful to change the appearance of how the API will respond to incorrect command usage.
     *
     * @param usageFormatter The new {@link UsageFormatter}
     */
    public void setUsageFormatter(@NotNull("usage formatter cannot be null") final UsageFormatter usageFormatter) {
        this.usageFormatter = usageFormatter;
    }

}
