package com.openmind.shared.mongodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code mongodb.*} configuration section (connection string + database name).
 */
@ConfigurationProperties(prefix = "mongodb")
public class MongoDbSettings {

    private String connectionString = "mongodb://localhost:27017";
    private String databaseName;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
