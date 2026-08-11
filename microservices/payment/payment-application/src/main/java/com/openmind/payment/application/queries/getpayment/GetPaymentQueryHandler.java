package com.openmind.payment.application.queries.getpayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.application.queries.QueryHandler;
import com.openmind.shared.application.queries.QueryResult;
import org.springframework.stereotype.Component;

@Component
public class GetPaymentQueryHandler implements QueryHandler<GetPaymentQuery, PaymentDto> {

    private final PaymentRepository paymentRepository;

    public GetPaymentQueryHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public QueryResult<PaymentDto> handle(GetPaymentQuery query) {
        return paymentRepository.findByOrderId(query.orderId())
                .map(p -> new PaymentDto(
                        p.getId(), p.getOrderId(), p.getCustomerId(), p.getAmount().getAmount(),
                        p.getStatus().getName(), p.getTransactionId(), p.getFailureReason()))
                .map(QueryResult::success)
                .orElseGet(() -> QueryResult.failure("Payment not found"));
    }
}
