package com.insurance.customer.adapter.out.event;

import com.insurance.customer.domain.port.out.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class EventPublisherAdapter implements EventPublisher {

    @Override
    public void publishCustomerCreated(UUID customerId) {
        log.info("Event: Customer created - {}", customerId);
    }

    @Override
    public void publishCustomerUpdated(UUID customerId) {
        log.info("Event: Customer updated - {}", customerId);
    }

    @Override
    public void publishCustomerDeleted(UUID customerId) {
        log.info("Event: Customer deleted - {}", customerId);
    }
}
