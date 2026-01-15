package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.DuplicateRequestException;
import com.insurance.policy.application.exception.PolicyAlreadyExistsException;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.port.in.ContractPolicyUseCase;
import com.insurance.policy.domain.port.out.CustomerValidator;
import com.insurance.policy.domain.port.out.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractPolicyService implements ContractPolicyUseCase {
    
    private final CustomerValidator customerValidator;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional
    public InsurancePolicy execute(UUID customerId, PolicyType policyType, String idempotencyKey) {
        log.info("Processing policy contract for customer: {} with idempotency key: {}", 
                 customerId, idempotencyKey);

        Optional<InsurancePolicy> existingPolicy = 
            policyRepository.findByIdempotencyKey(idempotencyKey);
        
        if (existingPolicy.isPresent()) {
            log.warn("Duplicate request detected with idempotency key: {}", idempotencyKey);
            throw new DuplicateRequestException(
                "This contract request was already processed", 
                idempotencyKey
            );
        }

        customerValidator.validateExists(customerId);
        
        if (policyRepository.existsActivePolicy(customerId)) {
            log.warn("Customer {} already has an active policy", customerId);
            throw new PolicyAlreadyExistsException(
                "Customer already has an active insurance policy"
            );
        }
        
        InsurancePolicy policy = buildPolicy(customerId, policyType, idempotencyKey);
        
        InsurancePolicy savedPolicy = policyRepository.save(policy);
        
        log.info("Policy contracted successfully: {} for customer: {}", 
                 savedPolicy.getPolicyNumber(), customerId);
        
        return savedPolicy;
    }
    
    private InsurancePolicy buildPolicy(UUID customerId, PolicyType policyType, String idempotencyKey) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        
        return InsurancePolicy.builder()
            .id(UUID.randomUUID())
            .customerId(customerId)
            .policyNumber(InsurancePolicy.generatePolicyNumber())
            .policyType(policyType)
            .coverageAmount(policyType.getCoverageAmount())
            .monthlyPremium(policyType.getMonthlyPremium())
            .status(PolicyStatus.ACTIVE)
            .startDate(startDate)
            .endDate(endDate)
            .idempotencyKey(idempotencyKey)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(0)
            .build();
    }
}
