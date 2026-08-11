package com.openmind.order.api.controllers;

import com.openmind.order.api.dto.CreateOrderRequest;
import com.openmind.order.application.commands.createorder.CreateOrderCommand;
import com.openmind.order.application.commands.createorder.OrderItemCommand;
import com.openmind.order.application.queries.getorder.GetOrderQuery;
import com.openmind.order.application.queries.getorder.OrderDto;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.application.commands.CommandResult;
import com.openmind.shared.application.queries.QueryBus;
import com.openmind.shared.application.queries.QueryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public OrderController(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") UUID id) {
        QueryResult<OrderDto> result = queryBus.send(new GetOrderQuery(id));
        return result.isSuccess()
                ? ResponseEntity.ok(result.getData())
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new OrderItemCommand(i.productId(), i.productName(), i.quantity(), i.unitPrice()))
                .toList();

        var command = new CreateOrderCommand(
                UUID.randomUUID(),
                request.customerId(),
                items,
                request.shippingAddress().street(),
                request.shippingAddress().city(),
                request.shippingAddress().state(),
                request.shippingAddress().zipCode(),
                request.shippingAddress().country());

        CommandResult<UUID> result = commandBus.send(command);

        return result.isSuccess()
                ? ResponseEntity.created(URI.create("/api/orders/" + result.getData()))
                        .body(Map.of("orderId", result.getData()))
                : ResponseEntity.badRequest().body(result.getErrorMessage());
    }
}
