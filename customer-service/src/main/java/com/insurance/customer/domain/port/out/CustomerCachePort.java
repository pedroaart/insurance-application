package com.insurance.customer.domain.port.out;

import com.insurance.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for cache operations.
 * Implements Cache-Aside pattern with versioning.
 */
public interface CustomerCachePort {
    
    /**
     * Gets customer from cache.
     * 
     * @param customerId the customer ID
     * @return optional containing cached customer
     */
    Optional<Customer> get(UUID customerId);
    
    /**
     * Puts customer in cache with version control (Fence Token pattern).
     * 
     * @param customer the customer to cache
     */
    void put(Customer customer);
    
    /**
     * Evicts customer from cache.
     * Should NOT be called within @Transactional context.
     * 
     * @param customerId the customer ID
     */
    void evict(UUID customerId);
    
    /**
     * Evicts all customers from cache.
     */
    void evictAll();
}
