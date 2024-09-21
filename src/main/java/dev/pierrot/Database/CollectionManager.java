package dev.pierrot.Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.pierrot.Main;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

public abstract class CollectionManager<T>  {
    protected final MongoDatabase database = Main.mongoClient.getDatabase("AnimalMusic");
    protected static MongoCollection<Document> collection;

    protected MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public abstract void upsert(T data);
    public abstract @Nullable T get(Document query);

    public abstract void delete(Document query);
}
