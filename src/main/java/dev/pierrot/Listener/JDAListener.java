package dev.pierrot.Listener;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.pierrot.App;
import dev.pierrot.Commands.PrefixCommand;
import dev.pierrot.Component.ButtonComponent;
import dev.pierrot.Handlers.GuildMusicManager;
import dev.pierrot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class JDAListener extends ListenerAdapter {
    public static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    private static final Logger LOG = Utils.getLogger(JDAListener.class);
    private final LavalinkClient client = App.client;

    public JDAListener() {}

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        PrefixCommand.loadCommands();
        ButtonComponent.loadButtonComponents();
        LOG.info("{} is ready!", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        PrefixCommand.handlePrefixCommand(client, event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonComponent.handleButtonComponent(client, event);
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        var guild = event.getGuild();
        var voiceChannel = event.getChannelLeft();
        var selfMember = guild.getSelfMember();

        if (voiceChannel != null && !event.getEntity().getUser().isBot()) {
            boolean botWasInChannel = voiceChannel.getMembers().stream()
                    .anyMatch(member -> member.equals(selfMember));

            if (botWasInChannel) {
                checkAndDisconnectIfAlone(guild, voiceChannel);
            }
        }
    }

    private void checkAndDisconnectIfAlone(Guild guild, AudioChannelUnion voiceChannel) {
        long botId = guild.getSelfMember().getIdLong();
        boolean isBotAlone = voiceChannel.getMembers().stream()
                .allMatch(member -> member.getIdLong() == botId);
        var guildId = guild.getIdLong();

        synchronized (this) {
            var guildMusicManager = musicManagers.get(guildId);

            if (guildMusicManager == null) return;

            if (isBotAlone) {
                guild.getJDA().getDirectAudioController().disconnect(guild);
                guildMusicManager.metadata.sendMessageEmbeds(new EmbedBuilder().setAuthor("Mọi người bỏ em một mình 🥹").build()).queue();
                musicManagers.remove(guild.getIdLong());
            }
        }


    }
}