package com.openmind.orchestrator.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmind.order.contract.commands.PlaceOrderCommand;
import com.openmind.orchestrator.api.saga.OrderPlacementSaga;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
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
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public SagaController(MessagePublisher messagePublisher, EntityManager entityManager, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/orders/{orderId}/place")
    public ResponseEntity<?> place(@PathVariable("orderId") UUID orderId) {
        messagePublisher.publish(Topics.ORDER_COMMANDS, new PlaceOrderCommand(orderId, orderId));
        return ResponseEntity.accepted().body(Map.of("orderId", orderId));
    }

    @GetMapping("/orders/{orderId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> get(@PathVariable("orderId") UUID orderId) {
        String sagaId;
        try {
            // Axon's JpaSagaStore schema: AssociationValueEntry (sagaId/sagaType/associationKey/
            // associationValue) indexes SagaEntry (sagaId/sagaType/revision/serializedSaga) rows.
            sagaId = entityManager.createQuery(
                            "SELECT a.sagaId FROM AssociationValueEntry a "
                                    + "WHERE a.sagaType = :sagaType AND a.associationKey = :key AND a.associationValue = :value",
                            String.class)
                    .setParameter("sagaType", OrderPlacementSaga.class.getName())
                    .setParameter("key", "orderId")
                    .setParameter("value", orderId.toString())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return ResponseEntity.notFound().build();
        }

        byte[] serializedSaga;
        try {
            serializedSaga = entityManager.createQuery(
                            "SELECT s.serializedSaga FROM SagaEntry s WHERE s.sagaId = :sagaId", byte[].class)
                    .setParameter("sagaId", sagaId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return ResponseEntity.notFound().build();
        }

        try {
            Object saga = objectMapper.readValue(new String(serializedSaga, StandardCharsets.UTF_8), Object.class);
            return ResponseEntity.ok(saga);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to read saga state"));
        }
    }
}
