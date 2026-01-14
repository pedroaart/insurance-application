package com.insurance.customer.domain.port.in;

import java.util.UUID;

/**
 * Input port for deleting customers.
 */
public interface DeleteCustomerUseCase {
    
    /**
     * Deletes a customer from the system.
     * 
     * @param customerId the customer ID to delete
     * @throws CustomerNotFoundException if customer not found
     */
    void execute(UUID customerId);
}
