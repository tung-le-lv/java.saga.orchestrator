package com.openmind.fulfillment.api.controllers;

import com.openmind.fulfillment.application.queries.getfulfillment.FulfillmentDto;
import com.openmind.fulfillment.application.queries.getfulfillment.GetFulfillmentQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/fulfillments")
public class FulfillmentController {

    private final QueryGateway queryGateway;

    public FulfillmentController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable("orderId") UUID orderId) {
        FulfillmentDto fulfillment = queryGateway.query(new GetFulfillmentQuery(orderId), ResponseTypes.instanceOf(FulfillmentDto.class)).join();
        return fulfillment != null
                ? ResponseEntity.ok(fulfillment)
                : ResponseEntity.notFound().build();
    }
}
