package dev.pierrot.Database.Model;

import dev.pierrot.Database.IModel;
import org.bson.Document;

public class VoiceModel extends IModel {
    public String guildId;
    public String voiceChannelId;

    public VoiceModel(String g, String v) {
        guildId = g;
        voiceChannelId = v;
    }

    public VoiceModel(Document data) {
        guildId = data.getString("guildId");
        voiceChannelId = data.getString("voiceChannelId");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("guildId", guildId)
                .append("voiceChannelId", voiceChannelId);

    }
}
