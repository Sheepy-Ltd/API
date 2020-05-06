package com.sheepybot.internal.command.defaults.admin;

import com.sheepybot.api.entities.command.Arguments;
import com.sheepybot.api.entities.command.CommandContext;
import com.sheepybot.api.entities.command.CommandExecutor;
import com.sheepybot.api.entities.messaging.Messaging;
import com.sheepybot.api.entities.metrics.ServerInfo;
import com.sheepybot.api.entities.utils.BotUtils;

import java.util.StringJoiner;

public class SystemInfoCommand implements CommandExecutor {

    @Override
    public boolean execute(final CommandContext context,
                           final Arguments args) {
        if (!BotUtils.isBotAdmin(context.getMember())) {
            context.reply(context.i18n("notBotAdmin"));
        } else {
            final StringJoiner joiner = new StringJoiner("\n");

            joiner.add("--------------- System Info ---------------");
            joiner.add(String.format("OS Name: %s", System.getProperty("os.name")));
            joiner.add(String.format("OS Architecture: %s", System.getProperty("os.arch")));
            joiner.add(String.format("OS Version: %s", System.getProperty("os.version")));
            joiner.add(String.format("Java Version: %s", System.getProperty("java.version")));
            joiner.add(String.format("Total Memory MB: %s", ServerInfo.getTotalMemory()));
            joiner.add(String.format("Used Memory MB: %s", ServerInfo.getUsedMemory()));
            joiner.add(String.format("Max Memory MB: %s", ServerInfo.getMaxMemory()));
            joiner.add(String.format("Thread Count: %s", ServerInfo.getTotalThreads()));
            joiner.add(String.format("Process CPU Load: %s", ServerInfo.getProcessCpuLoad()));
            joiner.add(String.format("System CPU Load: %s", ServerInfo.getSystemCpuLoad()));
            joiner.add(String.format("Disk Usage: %s", ServerInfo.getDiskUsage()));
            joiner.add(String.format("Disk Size: %s", ServerInfo.getDiskSize()));
            joiner.add(String.format("Core Count: %s", ServerInfo.getAvailableProcessors()));
            joiner.add("-------------------------------------------");

            context.reply(Messaging.getLocalMessageBuilder().appendCodeBlock(joiner.toString(), "text").build());
        }

        return true;
    }

}
