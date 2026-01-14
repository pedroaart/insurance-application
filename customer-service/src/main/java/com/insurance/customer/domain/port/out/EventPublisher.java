package com.insurance.customer.domain.port.out;

import java.util.UUID;

/**
 * Output port for event publishing using Transactional Outbox pattern.
 * Events are stored in the database and processed asynchronously.
 */
public interface EventPublisher {
    
    /**
     * Publishes a customer created event.
     * 
     * @param customerId the customer ID
     */
    void publishCustomerCreated(UUID customerId);
    
    /**
     * Publishes a customer updated event.
     * 
     * @param customerId the customer ID
     */
    void publishCustomerUpdated(UUID customerId);
    
    /**
     * Publishes a customer deleted event.
     * 
     * @param customerId the customer ID
     */
    void publishCustomerDeleted(UUID customerId);
}
