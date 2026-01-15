package com.insurance.customer.adapter.out.cache;

import com.insurance.customer.domain.model.Customer;
import com.insurance.customer.domain.port.out.CustomerCachePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CustomerCacheAdapter implements CustomerCachePort {

    @Override
    public Optional<Customer> get(UUID customerId) {
        log.debug("Cache not implemented yet - returning empty");
        return Optional.empty();
    }

    @Override
    public void put(Customer customer) {
        log.debug("Cache not implemented yet - skipping put");
    }

    @Override
    public void evict(UUID customerId) {
        log.debug("Cache not implemented yet - skipping evict");
    }

    @Override
    public void evictAll() {
        log.debug("Cache not implemented yet - skipping evictAll");
    }
}
