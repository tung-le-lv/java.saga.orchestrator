package com.openmind.orchestrator.api.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.AssociationValues;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MongoDB-backed {@link SagaStore}. Axon ships JPA/JDBC saga stores but no MongoDB one, so
 * this fills that gap: it persists each saga instance as a JSON blob (serialized with a
 * field-visibility Jackson mapper, independent of the domain-facing MongoDB POJO codec) plus
 * its association values, in the {@code order_placement_sagas} collection - matching the
 * original MassTransit-Mongo saga repository this replaces.
 */
public class MongoSagaStore implements SagaStore<Object> {

    private static final String COLLECTION_NAME = "order_placement_sagas";

    private final MongoCollection<Document> collection;
    private final ObjectMapper sagaObjectMapper;

    public MongoSagaStore(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME);
        this.sagaObjectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .setVisibility(VisibilityChecker.Std.defaultInstance()
                        .withFieldVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE)
                        .withIsGetterVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY));
    }

    @Override
    public Set<String> findSagas(Class<?> sagaType, AssociationValue associationValue) {
        Document filter = new Document("sagaType", sagaType.getName())
                .append("associations", new Document("$elemMatch",
                        new Document("key", associationValue.getKey()).append("value", associationValue.getValue())));

        Set<String> ids = new HashSet<>();
        for (Document doc : collection.find(filter)) {
            ids.add(doc.getString("_id"));
        }
        return ids;
    }

    @Override
    public <S> Entry<S> loadSaga(Class<S> sagaType, String sagaIdentifier) {
        Document doc = collection.find(Filters.eq("_id", sagaIdentifier)).first();
        if (doc == null) {
            return null;
        }

        S saga = deserialize(doc.getString("data"), sagaType);
        Set<AssociationValue> associations = toAssociationValues(doc);

        return new Entry<>() {
            @Override
            public Set<AssociationValue> associationValues() {
                return associations;
            }

            @Override
            public S saga() {
                return saga;
            }
        };
    }

    @Override
    public void deleteSaga(Class<?> sagaType, String sagaIdentifier, Set<AssociationValue> associationValues) {
        collection.deleteOne(Filters.eq("_id", sagaIdentifier));
    }

    @Override
    public void insertSaga(Class<?> sagaType, String sagaIdentifier, Object saga, Set<AssociationValue> associationValues) {
        collection.insertOne(toDocument(sagaType, sagaIdentifier, saga, associationValues));
    }

    @Override
    public void updateSaga(Class<?> sagaType, String sagaIdentifier, Object saga, AssociationValues associationValues) {
        Set<AssociationValue> current = new HashSet<>();
        associationValues.forEach(current::add);
        collection.replaceOne(Filters.eq("_id", sagaIdentifier), toDocument(sagaType, sagaIdentifier, saga, current));
    }

    private Document toDocument(Class<?> sagaType, String sagaIdentifier, Object saga, Set<AssociationValue> associationValues) {
        List<Document> associations = new ArrayList<>();
        for (AssociationValue av : associationValues) {
            associations.add(new Document("key", av.getKey()).append("value", av.getValue()));
        }

        return new Document("_id", sagaIdentifier)
                .append("sagaType", sagaType.getName())
                .append("data", serialize(saga))
                .append("associations", associations);
    }

    private Set<AssociationValue> toAssociationValues(Document doc) {
        List<Document> associations = doc.getList("associations", Document.class, List.of());
        Set<AssociationValue> result = new HashSet<>();
        for (Document a : associations) {
            result.add(new AssociationValue(a.getString("key"), a.getString("value")));
        }
        return result;
    }

    private String serialize(Object saga) {
        try {
            return sagaObjectMapper.writeValueAsString(saga);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize saga " + saga.getClass(), e);
        }
    }

    private <S> S deserialize(String json, Class<S> sagaType) {
        try {
            return sagaObjectMapper.readValue(json, sagaType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize saga " + sagaType, e);
        }
    }
}
