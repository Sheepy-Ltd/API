package com.sheepybot.internal.command;

import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandHandler;
import com.sheepybot.util.BotUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class DefaultCommandHandler implements CommandHandler {

    /**
     * @param context The {@link CommandContext}
     * @param args    The {@link Arguments}
     */
    public void handle(@NotNull(value = "context cannot be null") final CommandContext context,
                       @NotNull(value = "args cannot be null") final Arguments args) {

        final Command command = context.getCommand();
        final Member member = context.getMember();

        if (command.isOwnerOnly() && !BotUtils.isBotAdmin(member)) {
            context.reply(context.i18n("ownerOnlyCommand"));
        } else {

            for (final Permission permission : command.getBotPermissions()) {

                if (permission.name().startsWith("VOICE_")) {

                    final GuildVoiceState state = member.getVoiceState();
                    if (state == null || state.getChannel() == null) {
                        context.reply(context.i18n("voiceNoActivity"));
                        return;
                    } else if (!context.getSelfMember().hasPermission(state.getChannel(), permission)) {
                        context.reply(context.i18n("botNoVoicePermission", permission.getName(), state.getChannel().getName()));
                        return;
                    }

                } else if (!member.hasPermission(context.getChannel(), permission)) {
                    context.reply(context.i18n("botMissingPermission", permission.getName()));
                    return;
                }

            }

            for (final Permission permission : command.getUserPermissions()) {

                if (!member.hasPermission(permission)) {
                    context.reply(context.i18n("memberMissingPermission", permission.getName()));
                    return;
                }

            }

            if (context.getCommand().getPreExecutor().apply(context)) {
                command.getExecutor().execute(context, args);
            }

        }

    }

}
