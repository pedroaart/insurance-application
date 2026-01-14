package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

import java.util.List;
import java.util.UUID;

/**
 * Input port for retrieving customers.
 */
public interface GetCustomerUseCase {
    
    /**
     * Retrieves a customer by ID.
     * 
     * @param customerId the customer ID
     * @return the customer
     * @throws CustomerNotFoundException if customer not found
     */
    Customer findById(UUID customerId);
    
    /**
     * Retrieves a customer by CPF.
     * 
     * @param cpf the customer CPF
     * @return the customer
     * @throws CustomerNotFoundException if customer not found
     */
    Customer findByCpf(String cpf);
    
    /**
     * Retrieves all customers (paginated in production).
     * 
     * @return list of all customers
     */
    List<Customer> findAll();
}
