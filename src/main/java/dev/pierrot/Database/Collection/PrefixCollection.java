package dev.pierrot.Database.Collection;

import com.mongodb.client.model.UpdateOptions;
import dev.pierrot.Database.CollectionManager;
import dev.pierrot.Database.Model.PrefixModel;

import org.bson.Document;
import java.util.UUID;

public class PrefixCollection extends CollectionManager<PrefixModel> {

    public PrefixCollection() {
        collection = getCollection("prefix");
    }

    @Override
    public void upsert(PrefixModel data) {

        Document updatedDocument = new Document("$set", data.toDocument());

        collection.updateOne(
                new Document("_id", UUID.fromString(data.guildId)),
                updatedDocument,
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public PrefixModel get(Document query) {
        var data = collection.find(query).first() ;
        return data != null ? new PrefixModel(data) : null;
    }

    @Override
    public void delete(Document query) {
        collection.deleteOne(query);
    }
}
