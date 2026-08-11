package com.openmind.shared.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 * MongoDB codec/client conventions.
 * <p>
 * Aggregates and value objects are mapped automatically via reflection (Java property names
 * are already camelCase, so no name-translation convention is needed like in the original
 * .NET version). A property literally named {@code id} is mapped to the {@code _id} field by
 * the driver's default POJO conventions, same effect as the original {@code EntityIdConvention}.
 */
public final class MongoDbConventions {

    private MongoDbConventions() {
    }

    public static CodecRegistry codecRegistry() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true)
                .build();

        return CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(pojoCodecProvider));
    }

    public static MongoClientSettings clientSettings(String connectionString) {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry())
                .build();
    }
}
