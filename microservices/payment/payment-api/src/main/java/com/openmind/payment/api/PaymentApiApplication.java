package com.openmind.payment.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.payment", "com.openmind.shared"})
@EntityScan(basePackages = "com.openmind.payment")
@EnableJpaRepositories(basePackages = "com.openmind.payment")
public class PaymentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }
}
