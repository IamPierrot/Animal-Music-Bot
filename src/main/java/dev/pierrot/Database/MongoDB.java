package dev.pierrot.Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.pierrot.Main;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

final public class MongoDB {
    private final static String MONGO_URI = Main.config.getApp().MONGO_URI;
    public final MongoClient mongoClient = getMongoClient();

    private static MongoClient getMongoClient() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                MongoClientSettings.getDefaultCodecRegistry());

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGO_URI))
                .codecRegistry(codecRegistry)
                .applyToLoggerSettings(builder -> builder.maxDocumentLength(100))
                .build();
        return MongoClients.create(settings);
    }
}
