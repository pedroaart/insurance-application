package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.PolicyNotFoundException;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.port.in.GetPolicyUseCase;
import com.insurance.policy.domain.port.out.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class GetPolicyService implements GetPolicyUseCase {
    
    private final PolicyRepository policyRepository;

    @Override
    @Transactional(readOnly = true)
    public InsurancePolicy findById(UUID policyId) {
        log.info("Finding policy by ID: {}", policyId);
        
        return policyRepository.findById(policyId)
            .orElseThrow(() -> new PolicyNotFoundException(
                "Policy not found id: " + policyId
            ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsurancePolicy> findByCustomerId(UUID customerId) {
        log.info("Finding policies for customer: {}", customerId);
        
        return policyRepository.findByCustomerId(customerId);
    }
}
