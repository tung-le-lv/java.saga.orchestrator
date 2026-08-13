package com.openmind.payment.api.controllers;

import com.openmind.payment.application.commands.retrypayment.RetryPaymentCommand;
import com.openmind.payment.application.queries.getpayment.GetPaymentQuery;
import com.openmind.payment.application.queries.getpayment.PaymentDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public PaymentController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable("orderId") UUID orderId) {
        PaymentDto payment = queryGateway.query(new GetPaymentQuery(orderId), ResponseTypes.instanceOf(PaymentDto.class)).join();
        return payment != null
                ? ResponseEntity.ok(payment)
                : ResponseEntity.notFound().build();
    }

    /**
     * Manual "retry via Payment API" path: forces the latest failed payment attempt for an
     * order to succeed, unblocking a saga instance parked in PaymentNotPaid.
     */
    @PostMapping("/order/{orderId}/retry")
    public ResponseEntity<?> retry(@PathVariable("orderId") UUID orderId) {
        commandGateway.sendAndWait(new RetryPaymentCommand(orderId));
        return ResponseEntity.ok().build();
    }
}
