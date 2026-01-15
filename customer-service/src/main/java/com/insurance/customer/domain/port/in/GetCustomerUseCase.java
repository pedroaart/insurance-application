package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

import java.util.List;
import java.util.UUID;


public interface GetCustomerUseCase {

    Customer findById(UUID customerId);

    Customer findByCpf(String cpf);

    List<Customer> findAll();
}
