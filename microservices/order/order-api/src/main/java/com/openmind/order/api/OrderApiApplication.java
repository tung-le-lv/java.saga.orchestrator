package com.openmind.order.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.order", "com.openmind.shared"})
@EntityScan(basePackages = "com.openmind.order")
@EnableJpaRepositories(basePackages = "com.openmind.order")
public class OrderApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }
}
