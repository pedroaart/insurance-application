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
        
        // Validar se o cliente existe (integração back-to-back)
        // Circuit breaker está no CustomerClient
        customerValidator.validateExists(customerId);
        
        // Criar simulação baseada no tipo de apólice
        Simulation simulation = Simulation.fromPolicyType(customerId, policyType);
        
        // Persistir simulação para histórico e analytics
        Simulation savedSimulation = simulationRepository.save(simulation);
        
        log.info("Simulation created successfully: {} for customer: {}", 
                 savedSimulation.getId(), customerId);
        
        return savedSimulation;
    }
}
