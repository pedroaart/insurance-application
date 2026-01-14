package com.insurance.policy.domain.port.in;

import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.model.Simulation;

import java.util.UUID;

public interface SimulatePolicyUseCase {
    Simulation execute(UUID customerId, PolicyType policyType);
}
