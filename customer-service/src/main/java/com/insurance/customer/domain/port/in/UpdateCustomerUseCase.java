package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

import java.util.UUID;


public interface UpdateCustomerUseCase {

    Customer execute(UUID customerId, Customer customer);
}
