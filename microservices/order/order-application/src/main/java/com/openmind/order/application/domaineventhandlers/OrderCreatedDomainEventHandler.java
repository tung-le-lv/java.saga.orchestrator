package com.openmind.order.application.domaineventhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.openmind.order.domain.events.OrderCreatedDomainEvent;

@Component
public class OrderCreatedDomainEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedDomainEventHandler.class);

    @EventListener
    public void on(OrderCreatedDomainEvent event) {
        log.info("Order created successfully OrderId: {}", event.getOrderId());
    }
}
