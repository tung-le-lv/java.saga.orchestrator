package com.openmind.fulfillment.application.queries.getfulfillment;

import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import com.openmind.shared.application.queries.QueryHandler;
import com.openmind.shared.application.queries.QueryResult;
import org.springframework.stereotype.Component;

@Component
public class GetFulfillmentQueryHandler implements QueryHandler<GetFulfillmentQuery, FulfillmentDto> {

    private final FulfillmentRepository fulfillmentRepository;

    public GetFulfillmentQueryHandler(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @Override
    public QueryResult<FulfillmentDto> handle(GetFulfillmentQuery query) {
        return fulfillmentRepository.findByOrderId(query.orderId())
                .map(f -> new FulfillmentDto(
                        f.getId(), f.getOrderId(), f.getStatus().getName(),
                        f.getTrackingNumber(), f.getEstimatedDelivery(), f.getFailureReason()))
                .map(QueryResult::success)
                .orElseGet(() -> QueryResult.failure("Fulfillment not found"));
    }
}
