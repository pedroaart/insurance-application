package com.insurance.policy.adapter.out.integration;

import com.insurance.policy.application.exception.CustomerNotFoundException;
import com.insurance.policy.application.exception.ServiceUnavailableException;
import com.insurance.policy.domain.port.out.CustomerValidator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerClient implements CustomerValidator {

    private final RestTemplate restTemplate;

    @Value("${customer.service.url:http://customer-service:8080}")
    private String customerServiceUrl;

    @Override
    @CircuitBreaker(name = "customerService", fallbackMethod = "validateExistsFallback")
    public void validateExists(UUID customerId) {
        log.info("Validating customer existence: {}", customerId);

        try {
            String url = customerServiceUrl + "/api/v1/customers/" + customerId;
            restTemplate.getForObject(url, Object.class);
            log.info("Customer validated successfully: {}", customerId);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Customer not found: {}", customerId);
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        } catch (Exception e) {
            log.error("Error validating customer: {}", e.getMessage());
            throw new ServiceUnavailableException(
                    "Unable to validate customer. Please try again later.", e
            );
        }
    }

    @Override
    public boolean exists(UUID customerId) {
        try {
            validateExists(customerId);
            return true;
        } catch (CustomerNotFoundException e) {
            return false;
        }
    }

    private void validateExistsFallback(UUID customerId, Exception e) {
        log.error("Circuit breaker activated for customer validation. Customer ID: {}, Error: {}",
                customerId, e.getMessage());

        throw new ServiceUnavailableException(
                "Customer validation service is temporarily unavailable. Please try again later."
        );
    }

    private boolean existsFallback(UUID customerId, Exception e) {
        log.error("Circuit breaker activated for customer check: {}", e.getMessage());
        throw new ServiceUnavailableException(
                "Customer validation service is temporarily unavailable."
        );
    }
}