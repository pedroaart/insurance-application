package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

import java.util.UUID;

/**
 * Input port for updating customers.
 */
public interface UpdateCustomerUseCase {
    
    /**
     * Updates an existing customer.
     * 
     * @param customerId the customer ID to update
     * @param customer the updated customer data
     * @return the updated customer
     * @throws CustomerNotFoundException if customer not found
     * @throws IllegalArgumentException if data is invalid
     */
    Customer execute(UUID customerId, Customer customer);
}
