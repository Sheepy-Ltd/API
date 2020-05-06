package examples;

import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;

public class CommandExample implements CommandExecutor {

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {

        context.reply(String.format("Hello, %s!", context.getMember().getAsMention()));

        return true;
    }

}
