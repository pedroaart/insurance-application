package com.insurance.policy.adapter.out.persistence.mapper;

import com.insurance.policy.adapter.out.persistence.entity.SimulationEntity;
import com.insurance.policy.domain.model.Simulation;
import org.springframework.stereotype.Component;

@Component
public class SimulationPersistenceMapper {
    
    public SimulationEntity toEntity(Simulation simulation) {
        if (simulation == null) {
            return null;
        }
        
        return SimulationEntity.builder()
            .id(simulation.getId())
            .customerId(simulation.getCustomerId())
            .policyType(simulation.getPolicyType())
            .coverageAmount(simulation.getCoverageAmount())
            .monthlyPremium(simulation.getMonthlyPremium())
            .simulatedAt(simulation.getSimulatedAt())
            .build();
    }
    
    public Simulation toDomain(SimulationEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Simulation.builder()
            .id(entity.getId())
            .customerId(entity.getCustomerId())
            .policyType(entity.getPolicyType())
            .coverageAmount(entity.getCoverageAmount())
            .monthlyPremium(entity.getMonthlyPremium())
            .annualPremium(entity.getMonthlyPremium().multiply(java.math.BigDecimal.valueOf(12)))
            .simulatedAt(entity.getSimulatedAt())
            .build();
    }
}
