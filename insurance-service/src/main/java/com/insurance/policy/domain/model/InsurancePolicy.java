package com.insurance.policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicy {
    private UUID id;
    private UUID customerId;
    private String policyNumber;
    private PolicyType policyType;
    private BigDecimal coverageAmount;
    private BigDecimal monthlyPremium;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    public static String generatePolicyNumber() {
        return "POL-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}