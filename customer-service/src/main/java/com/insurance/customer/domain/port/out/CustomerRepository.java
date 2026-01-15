package com.insurance.customer.domain.port.out;

import com.insurance.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    Optional<Customer> findByCpf(String cpf);

    List<Customer> findAll();

    void deleteById(UUID customerId);

    boolean existsByCpf(String cpf);
}
