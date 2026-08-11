package com.openmind.email.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.openmind.email", "com.openmind.shared"})
public class EmailApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailApiApplication.class, args);
    }
}
