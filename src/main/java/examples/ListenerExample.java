package examples;

import com.sheepybot.api.entities.event.EventHandler;
import com.sheepybot.api.entities.event.EventListener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ListenerExample implements EventListener {

    @EventHandler
    public void onMemeberMessage(final GuildMessageReceivedEvent event) {

        final Member member = event.getMember();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();

        if (message.getContentRaw().equals("Hello!")) {
            channel.sendMessage(String.format("Hello, %s!", member.getEffectiveName())).queue();
        }

    }

}
