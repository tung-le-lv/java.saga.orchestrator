package com.openmind.fulfillment.api;

import com.openmind.shared.mongodb.MongoDbSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.fulfillment", "com.openmind.shared"})
@EnableConfigurationProperties(MongoDbSettings.class)
public class FulfillmentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FulfillmentApiApplication.class, args);
    }
}
