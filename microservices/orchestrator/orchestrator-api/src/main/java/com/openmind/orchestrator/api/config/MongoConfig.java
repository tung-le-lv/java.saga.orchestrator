package com.openmind.orchestrator.api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.openmind.orchestrator.api.saga.MongoSagaStore;
import com.openmind.shared.mongodb.MongoDbConventions;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean(destroyMethod = "close")
    public MongoClient mongoClient(@Value("${mongodb.connection-string}") String connectionString) {
        return MongoClients.create(MongoDbConventions.clientSettings(connectionString));
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient client, @Value("${mongodb.database-name}") String databaseName) {
        return client.getDatabase(databaseName);
    }

    @Bean
    public SagaStore<Object> mongoSagaStore(MongoDatabase database) {
        return new MongoSagaStore(database);
    }
}
