package com.openmind.payment.application.queries.getpayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetPaymentQueryHandler {

    private final PaymentRepository paymentRepository;

    public GetPaymentQueryHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @QueryHandler
    public PaymentDto handle(GetPaymentQuery query) {
        return paymentRepository.findByOrderId(query.orderId())
                .map(p -> new PaymentDto(
                        p.getId(), p.getOrderId(), p.getCustomerId(), p.getAmount().amount(),
                        p.getStatus().getDisplayName(), p.getTransactionId(), p.getFailureReason()))
                .orElse(null);
    }
}
