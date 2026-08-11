package com.openmind.order.api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.openmind.shared.mongodb.MongoDbConventions;
import com.openmind.shared.mongodb.MongoDbSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean(destroyMethod = "close")
    public MongoClient mongoClient(MongoDbSettings settings) {
        return MongoClients.create(MongoDbConventions.clientSettings(settings.getConnectionString()));
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient client, MongoDbSettings settings) {
        return client.getDatabase(settings.getDatabaseName());
    }
}
