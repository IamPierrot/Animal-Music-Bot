package dev.pierrot.Database.Model;

import dev.pierrot.Database.IModel;
import org.bson.Document;

public class PrefixModel extends IModel {
    public String guildId;
    public String prefix;

    public PrefixModel(String g, String p) {
        guildId = g;
        prefix = p;
    }

    public PrefixModel(Document data) {
        guildId = data.getString("guildId");
        prefix = data.getString("prefix");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("guildId", guildId)
                .append("prefix", prefix);
    }
}
