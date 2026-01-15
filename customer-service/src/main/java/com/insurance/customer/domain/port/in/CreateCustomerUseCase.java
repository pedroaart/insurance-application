package com.insurance.customer.domain.port.in;

import com.insurance.customer.domain.model.Customer;

public interface CreateCustomerUseCase {

    Customer execute(Customer customer);
}
