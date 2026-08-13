package com.openmind.orchestrator.api.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmind.orchestrator.api.saga.OrderPlacementSaga;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public SagaController(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
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
