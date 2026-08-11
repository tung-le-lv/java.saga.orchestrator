package com.openmind.fulfillment.api.controllers;

import com.openmind.fulfillment.application.queries.getfulfillment.FulfillmentDto;
import com.openmind.fulfillment.application.queries.getfulfillment.GetFulfillmentQuery;
import com.openmind.shared.application.queries.QueryBus;
import com.openmind.shared.application.queries.QueryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/fulfillments")
public class FulfillmentController {

    private final QueryBus queryBus;

    public FulfillmentController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable("orderId") UUID orderId) {
        QueryResult<FulfillmentDto> result = queryBus.send(new GetFulfillmentQuery(orderId));
        return result.isSuccess()
                ? ResponseEntity.ok(result.getData())
                : ResponseEntity.notFound().build();
    }
}
