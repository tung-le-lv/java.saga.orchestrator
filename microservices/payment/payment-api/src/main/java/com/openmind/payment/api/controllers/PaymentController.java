package com.openmind.payment.api.controllers;

import com.openmind.payment.application.commands.retrypayment.RetryPaymentCommand;
import com.openmind.payment.application.queries.getpayment.GetPaymentQuery;
import com.openmind.payment.application.queries.getpayment.PaymentDto;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.application.commands.CommandResult;
import com.openmind.shared.application.queries.QueryBus;
import com.openmind.shared.application.queries.QueryResult;
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

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public PaymentController(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrder(@PathVariable("orderId") UUID orderId) {
        QueryResult<PaymentDto> result = queryBus.send(new GetPaymentQuery(orderId));
        return result.isSuccess()
                ? ResponseEntity.ok(result.getData())
                : ResponseEntity.notFound().build();
    }

    /**
     * Manual "retry via Payment API" path: forces the latest failed payment attempt for an
     * order to succeed, unblocking a saga instance parked in PaymentNotPaid.
     */
    @PostMapping("/order/{orderId}/retry")
    public ResponseEntity<?> retry(@PathVariable("orderId") UUID orderId) {
        CommandResult<Void> result = commandBus.send(new RetryPaymentCommand(orderId));
        return result.isSuccess()
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(result.getErrorMessage());
    }
}
