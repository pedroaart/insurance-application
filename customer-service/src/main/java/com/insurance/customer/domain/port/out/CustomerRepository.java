package com.insurance.customer.domain.port.out;

import com.insurance.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for customer persistence operations.
 * This interface defines what the domain needs from the persistence layer.
 */
public interface CustomerRepository {
    
    /**
     * Saves a customer (create or update).
     * 
     * @param customer the customer to save
     * @return the saved customer
     */
    Customer save(Customer customer);
    
    /**
     * Finds a customer by ID.
     * 
     * @param customerId the customer ID
     * @return optional containing the customer if found
     */
    Optional<Customer> findById(UUID customerId);
    
    /**
     * Finds a customer by CPF.
     * 
     * @param cpf the customer CPF
     * @return optional containing the customer if found
     */
    Optional<Customer> findByCpf(String cpf);
    
    /**
     * Finds all customers.
     * 
     * @return list of all customers
     */
    List<Customer> findAll();
    
    /**
     * Deletes a customer by ID.
     * 
     * @param customerId the customer ID
     */
    void deleteById(UUID customerId);
    
    /**
     * Checks if a customer exists by CPF.
     * 
     * @param cpf the customer CPF
     * @return true if exists, false otherwise
     */
    boolean existsByCpf(String cpf);
}
