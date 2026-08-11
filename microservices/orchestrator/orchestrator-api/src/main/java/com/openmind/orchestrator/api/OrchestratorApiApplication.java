package com.openmind.orchestrator.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Deliberately narrower than "com.openmind.shared": shared-application's CommandBus/QueryBus
// beans collide by name with Axon's own auto-configured commandBus/queryBus beans, and the
// orchestrator doesn't use the command/query bus (or MongoDbContext/MongoRepository) at all -
// it only needs MessagePublisher/MessageDispatcher from shared-messaging.
@ComponentScan(basePackages = {"com.openmind.orchestrator", "com.openmind.shared.messaging"})
public class OrchestratorApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApiApplication.class, args);
    }
}
