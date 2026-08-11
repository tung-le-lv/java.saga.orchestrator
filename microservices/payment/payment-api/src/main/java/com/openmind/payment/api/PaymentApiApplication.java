package com.openmind.payment.api;

import com.openmind.shared.mongodb.MongoDbSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.payment", "com.openmind.shared"})
@EnableConfigurationProperties(MongoDbSettings.class)
public class PaymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }
}
