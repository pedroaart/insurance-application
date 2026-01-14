package com.insurance.policy.domain.port.in;

import com.insurance.policy.domain.model.InsurancePolicy;

import java.util.List;
import java.util.UUID;

public interface GetPolicyUseCase {
    InsurancePolicy findById(UUID policyId);
    List<InsurancePolicy> findByCustomerId(UUID customerId);
}
