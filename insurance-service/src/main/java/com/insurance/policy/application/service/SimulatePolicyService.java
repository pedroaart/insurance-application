package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.CustomerNotFoundException;
import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.model.Simulation;
import com.insurance.policy.domain.port.in.SimulatePolicyUseCase;
import com.insurance.policy.domain.port.out.CustomerValidator;
import com.insurance.policy.domain.port.out.SimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulatePolicyService implements SimulatePolicyUseCase {
    
    private final CustomerValidator customerValidator;
    private final SimulationRepository simulationRepository;

    @Override
    @Transactional
    public Simulation execute(UUID customerId, PolicyType policyType) {
        log.info("Simulating policy for customer: {} with type: {}", customerId, policyType);

        customerValidator.validateExists(customerId);
        
        Simulation simulation = Simulation.fromPolicyType(customerId, policyType);
        
        Simulation savedSimulation = simulationRepository.save(simulation);
        
        log.info("Simulation created successfully: {} for customer: {}", 
                 savedSimulation.getId(), customerId);
        
        return savedSimulation;
    }
}
