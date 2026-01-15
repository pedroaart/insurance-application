package com.insurance.customer.domain.port.out;

import com.insurance.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerCachePort {

    Optional<Customer> get(UUID customerId);

    void put(Customer customer);

    void evict(UUID customerId);

    void evictAll();
}
