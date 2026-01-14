package com.insurance.policy.adapter.out.persistence;

import com.insurance.policy.adapter.out.persistence.mapper.PolicyPersistenceMapper;
import com.insurance.policy.adapter.out.persistence.repository.JpaPolicyRepository;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.port.out.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyPersistenceAdapter implements PolicyRepository {
    
    private final JpaPolicyRepository jpaRepository;
    private final PolicyPersistenceMapper mapper;

    @Override
    public InsurancePolicy save(InsurancePolicy policy) {
        log.debug("Saving policy: {}", policy.getId());
        var entity = mapper.toEntity(policy);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<InsurancePolicy> findById(UUID policyId) {
        log.debug("Finding policy by ID: {}", policyId);
        return jpaRepository.findById(policyId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<InsurancePolicy> findByIdempotencyKey(String idempotencyKey) {
        log.debug("Finding policy by idempotency key: {}", idempotencyKey);
        return jpaRepository.findByIdempotencyKey(idempotencyKey)
            .map(mapper::toDomain);
    }

    @Override
    public List<InsurancePolicy> findByCustomerId(UUID customerId) {
        log.debug("Finding policies for customer: {}", customerId);
        return jpaRepository.findByCustomerId(customerId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsActivePolicy(UUID customerId) {
        log.debug("Checking if customer {} has active policy", customerId);
        return jpaRepository.existsByCustomerIdAndStatus(customerId, PolicyStatus.ACTIVE);
    }
}
