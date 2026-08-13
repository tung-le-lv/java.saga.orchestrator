package com.openmind.order.api.controllers;

import com.openmind.order.api.dto.CreateOrderRequest;
import com.openmind.order.application.commands.createorder.CreateOrderCommand;
import com.openmind.order.application.commands.createorder.OrderItemCommand;
import com.openmind.order.application.queries.getorder.GetOrderQuery;
import com.openmind.order.application.queries.getorder.OrderDto;
import com.openmind.order.contract.commands.PlaceOrderCommand;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
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

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final MessagePublisher messagePublisher;

    public OrderController(CommandGateway commandGateway, QueryGateway queryGateway, MessagePublisher messagePublisher) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.messagePublisher = messagePublisher;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") UUID id) {
        OrderDto order = queryGateway.query(new GetOrderQuery(id), ResponseTypes.instanceOf(OrderDto.class)).join();
        return order != null
                ? ResponseEntity.ok(order)
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

        UUID orderId = commandGateway.sendAndWait(command);

        return ResponseEntity.created(URI.create("/api/orders/" + orderId))
                .body(Map.of("orderId", orderId));
    }

    @PostMapping("/{id}/place")
    public ResponseEntity<?> placeOrder(@PathVariable("id") UUID id) {
        OrderDto order = queryGateway.query(new GetOrderQuery(id), ResponseTypes.instanceOf(OrderDto.class)).join();
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        messagePublisher.publish(Topics.ORDER_COMMANDS, new PlaceOrderCommand(id, id));

        return ResponseEntity.accepted().body(Map.of("orderId", id));
    }
}
