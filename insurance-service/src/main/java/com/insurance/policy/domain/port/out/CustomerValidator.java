package com.insurance.policy.domain.port.out;

import java.util.UUID;

public interface CustomerValidator {
    boolean exists(UUID customerId);
    void validateExists(UUID customerId);
}
