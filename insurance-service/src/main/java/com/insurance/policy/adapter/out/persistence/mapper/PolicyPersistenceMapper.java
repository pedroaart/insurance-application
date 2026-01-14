package com.insurance.policy.adapter.out.persistence.mapper;

import com.insurance.policy.adapter.out.persistence.entity.PolicyEntity;
import com.insurance.policy.domain.model.InsurancePolicy;
import org.springframework.stereotype.Component;

@Component
public class PolicyPersistenceMapper {
    
    public PolicyEntity toEntity(InsurancePolicy policy) {
        if (policy == null) {
            return null;
        }
        
        return PolicyEntity.builder()
            .id(policy.getId())
            .customerId(policy.getCustomerId())
            .policyNumber(policy.getPolicyNumber())
            .policyType(policy.getPolicyType())
            .coverageAmount(policy.getCoverageAmount())
            .monthlyPremium(policy.getMonthlyPremium())
            .status(policy.getStatus())
            .startDate(policy.getStartDate())
            .endDate(policy.getEndDate())
            .idempotencyKey(policy.getIdempotencyKey())
            .createdAt(policy.getCreatedAt())
            .updatedAt(policy.getUpdatedAt())
            .version(policy.getVersion())
            .build();
    }
    
    public InsurancePolicy toDomain(PolicyEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return InsurancePolicy.builder()
            .id(entity.getId())
            .customerId(entity.getCustomerId())
            .policyNumber(entity.getPolicyNumber())
            .policyType(entity.getPolicyType())
            .coverageAmount(entity.getCoverageAmount())
            .monthlyPremium(entity.getMonthlyPremium())
            .status(entity.getStatus())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .idempotencyKey(entity.getIdempotencyKey())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
