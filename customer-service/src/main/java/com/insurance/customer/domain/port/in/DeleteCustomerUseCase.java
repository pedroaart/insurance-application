package com.insurance.customer.domain.port.in;

import java.util.UUID;

public interface DeleteCustomerUseCase {

    void execute(UUID customerId);
}
