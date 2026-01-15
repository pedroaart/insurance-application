package com.insurance.customer.application.service;

import com.insurance.customer.application.exception.InvalidCustomerDataException;
import com.insurance.customer.application.exception.ServiceUnavailableException;
import com.insurance.customer.domain.model.Customer;
import com.insurance.customer.domain.port.in.*;
import com.insurance.customer.domain.port.out.CustomerCachePort;
import com.insurance.customer.domain.port.out.CustomerRepository;
import com.insurance.customer.domain.port.out.EventPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.insurance.customer.application.exception.CustomerAlreadyExistsException;
import com.insurance.customer.application.exception.CustomerNotFoundException;

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
    public Customer execute(Customer customer) {
        log.info("Creating customer with CPF: {}", maskCpf(customer.getCpf()));

        customer.sanitizeCpf();
        if (customer.getAddress() != null) {
            customer.getAddress().sanitizeZipCode();
        }

        validateCustomer(customer);

        if (customerRepository.existsByCpf(customer.getCpf())) {
            log.warn("Customer with CPF {} already exists", maskCpf(customer.getCpf()));
            throw new CustomerAlreadyExistsException("Customer with CPF already exists");
        }

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

        Customer savedCustomer = customerRepository.save(customer);

        eventPublisher.publishCustomerCreated(savedCustomer.getId());

        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "customerService", fallbackMethod = "findByIdFallback")
    public Customer findById(UUID customerId) {
        log.info("Finding customer by ID: {}", customerId);
        
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
    public Customer findByCpf(String cpf) {
        log.info("Finding customer by CPF: {}", maskCpf(cpf));

        String sanitizedCpf = cpf.replaceAll("\\D", "");

        return customerRepository.findByCpf(sanitizedCpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with CPF: " + maskCpf(sanitizedCpf)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        log.info("Finding all customers");
        return customerRepository.findAll();
    }

    @Override
    @Transactional
    public Customer execute(UUID customerId, Customer customer) {
        log.info("Updating customer with ID: {}", customerId);
        
        Customer existingCustomer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
        
        customer.sanitizeCpf();
        if (customer.getAddress() != null) {
            customer.getAddress().sanitizeZipCode();
        }
        
        validateCustomer(customer);
        
        if (!existingCustomer.getCpf().equals(customer.getCpf())
            && customerRepository.existsByCpf(customer.getCpf())) {
            throw new CustomerAlreadyExistsException("Customer with new CPF already exists");
        }
        
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
        
        eventPublisher.publishCustomerUpdated(updatedCustomer.getId());
        
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    @Override
    @Transactional
    public void execute(UUID customerId) {
        log.info("Deleting customer with ID: {}", customerId);
        
        if (!customerRepository.findById(customerId).isPresent()) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        
        customerRepository.deleteById(customerId);
        
        eventPublisher.publishCustomerDeleted(customerId);
        
        log.info("Customer deleted successfully with ID: {}", customerId);
    }


    private void validateCustomer(Customer customer) {
        if (!customer.isValidCpf()) {
            throw new InvalidCustomerDataException("Invalid CPF format or checksum");
        }

        if (!customer.isLegalAge()) {
            throw new InvalidCustomerDataException("Customer must be at least 18 years old");
        }

        if (customer.getAddress() != null && !customer.getAddress().isComplete()) {
            throw new InvalidCustomerDataException("Incomplete address information");
        }
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return "***";
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    private Customer findByIdFallback(UUID customerId, Exception e) {
        log.warn("Fallback triggered for findById, attempting to return from cache: {}", e.getMessage());
        return customerCache.get(customerId)
                .orElseThrow(() -> new ServiceUnavailableException("Customer service is temporarily unavailable"));
    }
}


