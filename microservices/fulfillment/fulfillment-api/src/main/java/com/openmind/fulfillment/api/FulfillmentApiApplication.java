package com.openmind.fulfillment.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.fulfillment", "com.openmind.shared"})
@EntityScan(basePackages = "com.openmind.fulfillment")
@EnableJpaRepositories(basePackages = "com.openmind.fulfillment")
public class FulfillmentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FulfillmentApiApplication.class, args);
    }
}
