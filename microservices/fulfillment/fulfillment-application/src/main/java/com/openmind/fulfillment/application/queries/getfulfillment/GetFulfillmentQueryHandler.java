package com.openmind.fulfillment.application.queries.getfulfillment;

import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetFulfillmentQueryHandler {

    private final FulfillmentRepository fulfillmentRepository;

    public GetFulfillmentQueryHandler(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @QueryHandler
    public FulfillmentDto handle(GetFulfillmentQuery query) {
        return fulfillmentRepository.findByOrderId(query.orderId())
                .map(f -> new FulfillmentDto(
                        f.getId(), f.getOrderId(), f.getStatus().getDisplayName(),
                        f.getTrackingNumber(), f.getEstimatedDelivery(), f.getFailureReason()))
                .orElse(null);
    }
}
