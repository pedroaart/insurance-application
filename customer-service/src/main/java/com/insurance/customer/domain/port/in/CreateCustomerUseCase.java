package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

/**
 * Input port (use case) for creating a new customer.
 * This defines the interface that the application core exposes to the outside world.
 */
public interface CreateCustomerUseCase {
    
    /**
     * Creates a new customer in the system.
     * 
     * @param customer the customer to create
     * @return the created customer with generated ID
     * @throws IllegalArgumentException if customer data is invalid
     * @throws CustomerAlreadyExistsException if CPF is already registered
     */
    Customer execute(Customer customer);
}
