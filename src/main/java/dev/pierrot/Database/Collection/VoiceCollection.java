package dev.pierrot.Database.Collection;

import com.mongodb.client.model.UpdateOptions;
import dev.pierrot.Database.CollectionManager;
import dev.pierrot.Database.Model.VoiceModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

public class VoiceCollection extends CollectionManager<VoiceModel> {
    public VoiceCollection() {
        collection = getCollection("voice");
    }
    @Override
    public void upsert(VoiceModel data) {
        if (data == null) return;
        Document updatedDocument = new Document("$set", data.toDocument());

        collection.updateOne(
                new Document("_id", ObjectId.get()),
                updatedDocument,
                new UpdateOptions().upsert(true)
        );

    }

    @Nullable
    @Override
    public VoiceModel get(Document query) {
        var data = collection.find(query).first();
        return data != null ? new VoiceModel(data) : null;
    }

    @Override
    public void delete(Document query) {
        collection.deleteOne(query);
    }
}
