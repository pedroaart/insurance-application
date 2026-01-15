package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.PolicyNotFoundException;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.port.out.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Policy Service Tests")
class GetPolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private GetPolicyService service;

    private UUID policyId;
    private UUID customerId;
    private InsurancePolicy policy;

    @BeforeEach
    void setUp() {
        policyId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        
        policy = InsurancePolicy.builder()
            .id(policyId)
            .customerId(customerId)
            .policyNumber("POL-2026-ABC123")
            .policyType(PolicyType.GOLD)
            .coverageAmount(new BigDecimal("200000.00"))
            .monthlyPremium(new BigDecimal("500.00"))
            .status(PolicyStatus.ACTIVE)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusYears(1))
            .build();
    }

    @Test
    @DisplayName("Should find policy by ID successfully")
    void shouldFindPolicyByIdSuccessfully() {
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));

        InsurancePolicy result = service.findById(policyId);

        assertNotNull(result);
        assertEquals(policyId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        verify(policyRepository).findById(policyId);
    }


    @Test
    @DisplayName("Should find all policies by customer ID")
    void shouldFindAllPoliciesByCustomerId() {
        List<InsurancePolicy> policies = Arrays.asList(policy);
        when(policyRepository.findByCustomerId(customerId)).thenReturn(policies);

        List<InsurancePolicy> result = service.findByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
        verify(policyRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return empty list when customer has no policies")
    void shouldReturnEmptyListWhenNoPolices() {
        when(policyRepository.findByCustomerId(customerId)).thenReturn(Arrays.asList());

        List<InsurancePolicy> result = service.findByCustomerId(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(policyRepository).findByCustomerId(customerId);
    }
}
