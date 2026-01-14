package com.insurance.policy.domain.port.out;

import com.insurance.policy.domain.model.InsurancePolicy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository {
    InsurancePolicy save(InsurancePolicy policy);
    Optional<InsurancePolicy> findById(UUID policyId);
    Optional<InsurancePolicy> findByIdempotencyKey(String idempotencyKey);
    List<InsurancePolicy> findByCustomerId(UUID customerId);
    boolean existsActivePolicy(UUID customerId);
}
