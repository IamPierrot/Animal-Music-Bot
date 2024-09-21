package dev.pierrot.Commands.Music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.pierrot.Commands.PrefixCommand;
import dev.pierrot.Database.Collection.VoiceCollection;
import dev.pierrot.Database.Model.VoiceModel;
import dev.pierrot.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.util.List;
import java.util.Objects;

public class Voice extends PrefixCommand {

    static {
        registerCommand(new Voice());
    }

    private Voice() {
        super("247", "làm cho bot treo voice 24/7", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
        aliases = new String[]{"choivoiem"};
        usage = "%s %s".formatted(Main.config.getApp().prefix, name);
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var voiceCollection = new VoiceCollection();
        var voiceChannelId = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel()).getId();
        var query = new Document("voiceChannelId", voiceChannelId);
        var voiceData = voiceCollection.get(query);
        if (voiceData != null) {
            voiceCollection.delete(query);
            event.getMessage().reply("Đã tắt treo voice 24/7 cho tui roài:3").queue();
            return;
        }

        var newData = new VoiceModel(event.getGuild().getId(), voiceChannelId);
        voiceCollection.upsert(newData);
        event.getMessage().reply("Bạn đã cho tôi ở 24/7 trong voice, làm gì làm đi (　o=^•ェ•)o　┏━┓").queue();
    }
}
