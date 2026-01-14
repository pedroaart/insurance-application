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

/**
 * Service responsible for contracting insurance policies.
 * 
 * KEY FINTECH PATTERNS:
 * 1. Idempotency: Uses idempotency key to prevent duplicate contracts
 * 2. Deduplication: Checks if customer already has active policy
 * 3. Fail-fast: Validates customer exists before processing
 * 4. Transactional integrity: All-or-nothing guarantee
 */
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
        
        // 1. IDEMPOTENCY CHECK - Critical for financial operations
        // If this exact request was already processed, return the existing result
        Optional<InsurancePolicy> existingPolicy = 
            policyRepository.findByIdempotencyKey(idempotencyKey);
        
        if (existingPolicy.isPresent()) {
            log.warn("Duplicate request detected with idempotency key: {}", idempotencyKey);
            throw new DuplicateRequestException(
                "This contract request was already processed", 
                idempotencyKey
            );
        }
        
        // 2. CUSTOMER VALIDATION - Back-to-back integration
        // Circuit breaker protects us from cascading failures
        customerValidator.validateExists(customerId);
        
        // 3. BUSINESS RULE - Prevent multiple active policies per customer
        if (policyRepository.existsActivePolicy(customerId)) {
            log.warn("Customer {} already has an active policy", customerId);
            throw new PolicyAlreadyExistsException(
                "Customer already has an active insurance policy"
            );
        }
        
        // 4. CREATE POLICY - All validations passed
        InsurancePolicy policy = buildPolicy(customerId, policyType, idempotencyKey);
        
        // 5. PERSIST - Transactional boundary ensures consistency
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
