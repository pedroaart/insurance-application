package com.insurance.customer.application.service;

import com.insurance.customer.domain.model.Customer;
import com.insurance.customer.domain.port.in.*;
import com.insurance.customer.domain.port.out.CustomerCachePort;
import com.insurance.customer.domain.port.out.CustomerRepository;
import com.insurance.customer.domain.port.out.EventPublisher;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insurance.customer.application.exception.CustomerAlreadyExistsException;
import com.insurance.customer.application.exception.CustomerNotFoundException;
import com.insurance.customer.application.exception.ServiceUnavailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing all customer use cases.
 * This service contains business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements 
    CreateCustomerUseCase, 
    GetCustomerUseCase, 
    UpdateCustomerUseCase, 
    DeleteCustomerUseCase {
    
    private final CustomerRepository customerRepository;
    private final CustomerCachePort customerCache;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    @CircuitBreaker(name = "customerService", fallbackMethod = "createCustomerFallback")
    @RateLimiter(name = "customerService")
    @Bulkhead(name = "customerService")
    public Customer execute(Customer customer) {
        log.info("Creating customer with CPF: {}", maskCpf(customer.getCpf()));
        
        // Sanitize and validate
        customer.sanitizeCpf();
        if (customer.getAddress() != null) {
            customer.getAddress().sanitizeZipCode();
        }
        
        validateCustomer(customer);
        
        // Check if CPF already exists
        if (customerRepository.existsByCpf(customer.getCpf())) {
            log.warn("Customer with CPF {} already exists", maskCpf(customer.getCpf()));
            throw new CustomerAlreadyExistsException("Customer with CPF already exists");
        }
        
        // Set timestamps
        customer.setId(UUID.randomUUID());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setVersion(0);
        
        if (customer.getAddress() != null) {
            customer.getAddress().setId(UUID.randomUUID());
            customer.getAddress().setCustomerId(customer.getId());
            customer.getAddress().setCreatedAt(LocalDateTime.now());
            customer.getAddress().setUpdatedAt(LocalDateTime.now());
        }
        
        // Save to database (within transaction)
        Customer savedCustomer = customerRepository.save(customer);
        
        // Publish event for asynchronous cache invalidation (Transactional Outbox)
        // Never evict cache within @Transactional to avoid dirty cache
        eventPublisher.publishCustomerCreated(savedCustomer.getId());
        
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "customerService", fallbackMethod = "findByIdFallback")
    public Customer findById(UUID customerId) {
        log.info("Finding customer by ID: {}", customerId);
        
        // Cache-Aside pattern with version control
        return customerCache.get(customerId)
            .or(() -> {
                Optional<Customer> customer = customerRepository.findById(customerId);
                customer.ifPresent(customerCache::put);
                return customer;
            })
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "customerService", fallbackMethod = "findByCpfFallback")
    public Customer findByCpf(String cpf) {
        log.info("Finding customer by CPF: {}", maskCpf(cpf));
        
        String sanitizedCpf = cpf.replaceAll("\\D", "");
        
        return customerRepository.findByCpf(sanitizedCpf)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with CPF: " + maskCpf(sanitizedCpf)));
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "customerService", fallbackMethod = "findAllFallback")
    @RateLimiter(name = "customerService")
    public List<Customer> findAll() {
        log.info("Finding all customers");
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "customerService", fallbackMethod = "updateCustomerFallback")
    @RateLimiter(name = "customerService")
    public Customer execute(UUID customerId, Customer customer) {
        log.info("Updating customer with ID: {}", customerId);
        
        Customer existingCustomer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        
        // Sanitize and validate
        customer.sanitizeCpf();
        if (customer.getAddress() != null) {
            customer.getAddress().sanitizeZipCode();
        }
        
        validateCustomer(customer);
        
        // Check if CPF is being changed and if it already exists
        if (!existingCustomer.getCpf().equals(customer.getCpf()) 
            && customerRepository.existsByCpf(customer.getCpf())) {
            throw new CustomerAlreadyExistsException("Customer with new CPF already exists");
        }
        
        // Update fields
        existingCustomer.setCpf(customer.getCpf());
        existingCustomer.setName(customer.getName());
        existingCustomer.setBirthDate(customer.getBirthDate());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        existingCustomer.setVersion(existingCustomer.getVersion() + 1);
        
        if (customer.getAddress() != null) {
            if (existingCustomer.getAddress() == null) {
                customer.getAddress().setId(UUID.randomUUID());
                customer.getAddress().setCustomerId(existingCustomer.getId());
                customer.getAddress().setCreatedAt(LocalDateTime.now());
            } else {
                customer.getAddress().setId(existingCustomer.getAddress().getId());
                customer.getAddress().setCustomerId(existingCustomer.getId());
                customer.getAddress().setCreatedAt(existingCustomer.getAddress().getCreatedAt());
            }
            customer.getAddress().setUpdatedAt(LocalDateTime.now());
            existingCustomer.setAddress(customer.getAddress());
        }
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        
        // Publish event for cache invalidation
        eventPublisher.publishCustomerUpdated(updatedCustomer.getId());
        
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    @Override
    @Transactional
    @CircuitBreaker(name = "customerService", fallbackMethod = "deleteCustomerFallback")
    @RateLimiter(name = "customerService")
    public void execute(UUID customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        
        if (!customerRepository.findById(customerId).isPresent()) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        
        customerRepository.deleteById(customerId);
        
        // Publish event for cache eviction
        eventPublisher.publishCustomerDeleted(customerId);
        
        log.info("Customer deleted successfully with ID: {}", customerId);
    }

    /**
     * Validates customer business rules
     */
    private void validateCustomer(Customer customer) {
        if (!customer.isValidCpf()) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        
        if (!customer.isLegalAge()) {
            throw new IllegalArgumentException("Customer must be at least 18 years old");
        }
        
        if (customer.getAddress() != null && !customer.getAddress().isComplete()) {
            throw new IllegalArgumentException("Incomplete address information");
        }
    }

    /**
     * Masks CPF for logging (LGPD compliance)
     */
    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return "***";
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    // Fallback methods for Circuit Breaker
    
    private Customer createCustomerFallback(Customer customer, Exception e) {
        log.error("Fallback triggered for createCustomer: {}", e.getMessage());
        throw new ServiceUnavailableException("Customer service is temporarily unavailable. Please try again later.");
    }

    private Customer findByIdFallback(UUID customerId, Exception e) {
        log.error("Fallback triggered for findById: {}", e.getMessage());
        // Try to return from cache as last resort
        return customerCache.get(customerId)
            .orElseThrow(() -> new ServiceUnavailableException("Customer service is temporarily unavailable"));
    }

    private Customer findByCpfFallback(String cpf, Exception e) {
        log.error("Fallback triggered for findByCpf: {}", e.getMessage());
        throw new ServiceUnavailableException("Customer service is temporarily unavailable");
    }

    private List<Customer> findAllFallback(Exception e) {
        log.error("Fallback triggered for findAll: {}", e.getMessage());
        throw new ServiceUnavailableException("Customer service is temporarily unavailable");
    }

    private Customer updateCustomerFallback(UUID customerId, Customer customer, Exception e) {
        log.error("Fallback triggered for updateCustomer: {}", e.getMessage());
        throw new ServiceUnavailableException("Customer service is temporarily unavailable");
    }

    private void deleteCustomerFallback(UUID customerId, Exception e) {
        log.error("Fallback triggered for deleteCustomer: {}", e.getMessage());
        throw new ServiceUnavailableException("Customer service is temporarily unavailable");
    }
}


