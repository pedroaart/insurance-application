package com.insurance.policy.adapter.out.persistence.entity;

import com.insurance.policy.domain.model.PolicyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "policy_simulations", indexes = {
    @Index(name = "idx_simulation_customer_id", columnList = "customer_id"),
    @Index(name = "idx_simulated_at", columnList = "simulated_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationEntity {
    
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "customer_id", nullable = false, columnDefinition = "UUID")
    private UUID customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 10)
    private PolicyType policyType;
    
    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;
    
    @Column(name = "monthly_premium", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPremium;
    
    @Column(name = "simulated_at", nullable = false)
    private LocalDateTime simulatedAt;
}
