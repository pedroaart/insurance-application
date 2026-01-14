package com.insurance.policy.adapter.out.integration;

import com.insurance.policy.application.exception.CustomerNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerClient {

    private final RestTemplate restTemplate;

    @Value("${customer.service.url:http://customer-service:8080}")
    private String customerServiceUrl;

    @CircuitBreaker(name = "customerService", fallbackMethod = "validateCustomerFallback")
    public boolean validateCustomerExists(UUID customerId) {
        log.info("Validating if customer exists: {}", customerId);

        try {
            String url = customerServiceUrl + "/api/v1/customers/" + customerId;
            restTemplate.getForObject(url, Object.class);
            log.info("Customer validated successfully: {}", customerId);
            return true;
        } catch (Exception e) {
            log.error("Customer not found or service unavailable: {}", customerId);
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
    }

    private boolean validateCustomerFallback(UUID customerId, Exception e) {
        log.error("Circuit breaker activated for customer validation: {}", e.getMessage());
        throw new CustomerNotFoundException("Customer service is temporarily unavailable. Please try again later.");
    }
}