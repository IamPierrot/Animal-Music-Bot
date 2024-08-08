package dev.pierrot.Commands.Music;


import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;
import dev.pierrot.Commands.PrefixCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class MusicQueue extends PrefixCommand {

    static  {
        PrefixCommand.registerCommand(new MusicQueue());
    }

    private MusicQueue() {
        super("queue", "xem hàng chờ", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = false;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guild = event.getGuild();
        var guildMusicManger = getOrCreateMusicManager(guild.getIdLong());

        if (!Objects.requireNonNull(guildMusicManger).isPlaying()) return;

        var queue = guildMusicManger.scheduler.queue;

        final String[] methods = {"", "🔁", "🔂"};

        final int songCount = queue.size();
        String nextSongs = songCount > 5 ? "Và **%d** bài khác nữa...".formatted(songCount - 5) : "Đang trong hàng chờ được phát là **%d** bài hát...".formatted(songCount);

        var tracks = formatTracks(queue);
        var currentTrack = guildMusicManger.getCurrentTrack();

        var embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor("Danh sách hàng chờ - %s".formatted(guild.getName()), null, event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(guild.getIconUrl())
                .setDescription("Đang phát **[%s](%s)** %s \n\n%s\n\n%s".formatted(
                        currentTrack.getInfo().getTitle(),
                        currentTrack.getInfo().getUri(),
                        methods[guildMusicManger.scheduler.getLoopMode()],
                        String.join("\n", tracks.subList(0, Math.min(songCount, 5))),
                        nextSongs
                ))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", event.getJDA().getSelfUser().getAvatarUrl());

        event.getMessage().replyEmbeds(embed.build()).queue();
    }

    private List<String> formatTracks(Queue<Track> queue) {
        int[] index = {1};
        return queue.stream()
                .map(track -> String.format("**%d** - %s", index[0]++, formatTrack(track)))
                .collect(Collectors.toList());
    }

    private String formatTrack(Track track) {
        return String.format("`%s | %s`", track.getInfo().getTitle(), track.getInfo().getAuthor());
    }

}
