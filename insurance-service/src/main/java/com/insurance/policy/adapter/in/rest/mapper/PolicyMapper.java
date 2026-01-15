package com.insurance.policy.adapter.in.rest.mapper;

import com.insurance.policy.adapter.in.rest.dto.PolicyResponse;
import com.insurance.policy.adapter.in.rest.dto.SimulationResponse;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.Simulation;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {
    
    public SimulationResponse toSimulationResponse(Simulation simulation) {
        if (simulation == null) {
            return null;
        }
        
        return SimulationResponse.builder()
            .simulationId(simulation.getId())
            .customerId(simulation.getCustomerId())
            .policyType(simulation.getPolicyType())
            .coverageAmount(simulation.getCoverageAmount())
            .monthlyPremium(simulation.getMonthlyPremium())
            .annualPremium(simulation.getAnnualPremium())
            .simulatedAt(simulation.getSimulatedAt())
            .message("Simulation created successfully. Use this information to proceed with the contract.")
            .build();
    }
    
    public PolicyResponse toPolicyResponse(InsurancePolicy policy) {
        if (policy == null) {
            return null;
        }
        
        return PolicyResponse.builder()
            .id(policy.getId())
            .customerId(policy.getCustomerId())
            .policyNumber(policy.getPolicyNumber())
            .policyType(policy.getPolicyType())
            .coverageAmount(policy.getCoverageAmount())
            .monthlyPremium(policy.getMonthlyPremium())
            .status(policy.getStatus())
            .startDate(policy.getStartDate())
            .endDate(policy.getEndDate())
            .createdAt(policy.getCreatedAt())
            .build();
    }
}
