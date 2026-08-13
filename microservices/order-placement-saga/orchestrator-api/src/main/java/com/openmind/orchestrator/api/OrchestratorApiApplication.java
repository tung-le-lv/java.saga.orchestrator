package com.openmind.orchestrator.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// MessagePublisher/MessageDispatcher and friends live in com.openmind.shared.messaging, outside
// this service's own base package.
@ComponentScan(basePackages = {"com.openmind.orchestrator", "com.openmind.shared.messaging"})
// Axon's JpaSagaStore and JpaTokenStore are autoconfigured once an EntityManagerFactory is
// present, but their SagaEntry/AssociationValueEntry/TokenEntry JPA entities live outside this
// service's base package, so they need an explicit entity scan to be picked up by the
// persistence unit.
@EntityScan(basePackages = {
        "com.openmind.orchestrator",
        "org.axonframework.modelling.saga.repository.jpa",
        "org.axonframework.eventhandling.tokenstore.jpa"
})
public class OrchestratorApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApiApplication.class, args);
    }
}
