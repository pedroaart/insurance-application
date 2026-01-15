package com.insurance.policy.application.service;

import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.model.Simulation;
import com.insurance.policy.domain.port.out.CustomerValidator;
import com.insurance.policy.domain.port.out.SimulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Simulate Policy Service Tests")
class SimulatePolicyServiceTest {

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private SimulationRepository simulationRepository;

    @InjectMocks
    private SimulatePolicyService service;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should simulate Bronze policy successfully")
    void shouldSimulateBronzePolicySuccessfully() {
        PolicyType policyType = PolicyType.BRONZE;
        doNothing().when(customerValidator).validateExists(customerId);
        when(simulationRepository.save(any(Simulation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Simulation result = service.execute(customerId, policyType);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(policyType, result.getPolicyType());
        assertEquals(policyType.getCoverageAmount(), result.getCoverageAmount());
        assertEquals(policyType.getMonthlyPremium(), result.getMonthlyPremium());
        assertEquals(policyType.getAnnualPremium(), result.getAnnualPremium());
        assertNotNull(result.getSimulatedAt());
        
        verify(customerValidator).validateExists(customerId);
        verify(simulationRepository).save(any(Simulation.class));
    }

    @Test
    @DisplayName("Should simulate Silver policy successfully")
    void shouldSimulateSilverPolicySuccessfully() {
        PolicyType policyType = PolicyType.SILVER;
        doNothing().when(customerValidator).validateExists(customerId);
        when(simulationRepository.save(any(Simulation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Simulation result = service.execute(customerId, policyType);

        assertEquals(policyType.getCoverageAmount(), result.getCoverageAmount());
        assertEquals(policyType.getMonthlyPremium(), result.getMonthlyPremium());
    }
}
