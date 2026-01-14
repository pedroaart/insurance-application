package com.insurance.policy.domain.port.in;

import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyType;

import java.util.UUID;

public interface ContractPolicyUseCase {
    InsurancePolicy execute(UUID customerId, PolicyType policyType, String idempotencyKey);
}
