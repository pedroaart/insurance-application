package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.CustomerNotFoundException;
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
        // Given
        PolicyType policyType = PolicyType.BRONZE;
        doNothing().when(customerValidator).validateExists(customerId);
        when(simulationRepository.save(any(Simulation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Simulation result = service.execute(customerId, policyType);

        // Then
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
        // Given
        PolicyType policyType = PolicyType.SILVER;
        doNothing().when(customerValidator).validateExists(customerId);
        when(simulationRepository.save(any(Simulation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Simulation result = service.execute(customerId, policyType);

        // Then
        assertEquals(policyType.getCoverageAmount(), result.getCoverageAmount());
        assertEquals(policyType.getMonthlyPremium(), result.getMonthlyPremium());
    }

    @Test
    @DisplayName("Should simulate Gold policy successfully")
    void shouldSimulateGoldPolicySuccessfully() {
        // Given
        PolicyType policyType = PolicyType.GOLD;
        doNothing().when(customerValidator).validateExists(customerId);
        when(simulationRepository.save(any(Simulation.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Simulation result = service.execute(customerId, policyType);

        // Then
        assertEquals(policyType.getCoverageAmount(), result.getCoverageAmount());
        assertEquals(policyType.getMonthlyPremium(), result.getMonthlyPremium());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        doThrow(new CustomerNotFoundException("Customer not found"))
            .when(customerValidator).validateExists(customerId);

        // When & Then
        assertThrows(
            CustomerNotFoundException.class,
            () -> service.execute(customerId, PolicyType.BRONZE)
        );

        verify(customerValidator).validateExists(customerId);
        verify(simulationRepository, never()).save(any());
    }
}
