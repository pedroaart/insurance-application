package com.insurance.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {
    private UUID id;
    private UUID customerId;
    private PolicyType policyType;
    private BigDecimal coverageAmount;
    private BigDecimal monthlyPremium;
    private BigDecimal annualPremium;
    private LocalDateTime simulatedAt;

    public static Simulation fromPolicyType(UUID customerId, PolicyType policyType) {
        return Simulation.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .policyType(policyType)
                .coverageAmount(policyType.getCoverageAmount())
                .monthlyPremium(policyType.getMonthlyPremium())
                .annualPremium(policyType.getAnnualPremium())
                .simulatedAt(LocalDateTime.now())
                .build();
    }
}