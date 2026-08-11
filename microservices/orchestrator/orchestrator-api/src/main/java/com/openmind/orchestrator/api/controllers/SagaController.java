package com.openmind.orchestrator.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.openmind.order.contract.commands.PlaceOrderCommand;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Starts (or retries) an order placement saga, and lets you inspect its current state.
 * Publishing {@link PlaceOrderCommand} here goes through the same SNS/SQS path as every
 * other saga input - it's just a convenience trigger, not a shortcut into the saga.
 */
@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final MessagePublisher messagePublisher;
    private final MongoDatabase mongoDatabase;
    private final ObjectMapper objectMapper;

    public SagaController(MessagePublisher messagePublisher, MongoDatabase mongoDatabase, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.mongoDatabase = mongoDatabase;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/orders/{orderId}/place")
    public ResponseEntity<?> place(@PathVariable("orderId") UUID orderId) {
        messagePublisher.publish(Topics.ORDER_COMMANDS, new PlaceOrderCommand(orderId, orderId));
        return ResponseEntity.accepted().body(Map.of("orderId", orderId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> get(@PathVariable("orderId") UUID orderId) {
        Document doc = mongoDatabase.getCollection("order_placement_sagas")
                .find(Filters.eq("associations", new Document("$elemMatch",
                        new Document("key", "orderId").append("value", orderId.toString()))))
                .first();

        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Object saga = objectMapper.readValue(doc.getString("data"), Object.class);
            return ResponseEntity.ok(saga);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to read saga state"));
        }
    }
}
