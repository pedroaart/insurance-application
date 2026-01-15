package com.insurance.policy.application.service;

import com.insurance.policy.application.exception.CustomerNotFoundException;
import com.insurance.policy.application.exception.DuplicateRequestException;
import com.insurance.policy.application.exception.PolicyAlreadyExistsException;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.port.out.CustomerValidator;
import com.insurance.policy.domain.port.out.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Contract Policy Service Tests")
class ContractPolicyServiceTest {

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private ContractPolicyService service;

    private UUID customerId;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        idempotencyKey = "contract-" + UUID.randomUUID();
    }

    @Test
    @DisplayName("Should contract policy successfully")
    void shouldContractPolicySuccessfully() {
        PolicyType policyType = PolicyType.GOLD;

        when(policyRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.empty());
        doNothing().when(customerValidator).validateExists(customerId);
        when(policyRepository.existsActivePolicy(customerId)).thenReturn(false);
        when(policyRepository.save(any(InsurancePolicy.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        InsurancePolicy result = service.execute(customerId, policyType, idempotencyKey);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(policyType, result.getPolicyType());
        assertEquals(PolicyStatus.ACTIVE, result.getStatus());
        assertEquals(policyType.getCoverageAmount(), result.getCoverageAmount());
        assertEquals(policyType.getMonthlyPremium(), result.getMonthlyPremium());
        assertNotNull(result.getPolicyNumber());
        assertEquals(idempotencyKey, result.getIdempotencyKey());

        verify(customerValidator).validateExists(customerId);
        verify(policyRepository).existsActivePolicy(customerId);
        verify(policyRepository).save(any(InsurancePolicy.class));
    }

    @Test
    @DisplayName("Should throw DuplicateRequestException when idempotency key exists")
    void shouldThrowExceptionWhenIdempotencyKeyExists() {
        InsurancePolicy existingPolicy = InsurancePolicy.builder()
            .id(UUID.randomUUID())
            .idempotencyKey(idempotencyKey)
            .build();

        when(policyRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.of(existingPolicy));

        DuplicateRequestException exception = assertThrows(
            DuplicateRequestException.class,
            () -> service.execute(customerId, PolicyType.BRONZE, idempotencyKey)
        );

        assertEquals("This contract request was already processed", exception.getMessage());
        assertEquals(idempotencyKey, exception.getIdempotencyKey());

        verify(policyRepository).findByIdempotencyKey(idempotencyKey);
        verify(customerValidator, never()).validateExists(any());
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(policyRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.empty());
        doThrow(new CustomerNotFoundException("Customer not found"))
            .when(customerValidator).validateExists(customerId);

        assertThrows(
            CustomerNotFoundException.class,
            () -> service.execute(customerId, PolicyType.SILVER, idempotencyKey)
        );

        verify(customerValidator).validateExists(customerId);
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PolicyAlreadyExistsException when customer has active policy")
    void shouldThrowExceptionWhenCustomerHasActivePolicy() {
        when(policyRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.empty());
        doNothing().when(customerValidator).validateExists(customerId);
        when(policyRepository.existsActivePolicy(customerId)).thenReturn(true);

        PolicyAlreadyExistsException exception = assertThrows(
            PolicyAlreadyExistsException.class,
            () -> service.execute(customerId, PolicyType.GOLD, idempotencyKey)
        );

        assertEquals("Customer already has an active insurance policy", exception.getMessage());

        verify(customerValidator).validateExists(customerId);
        verify(policyRepository).existsActivePolicy(customerId);
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create policy with correct dates")
    void shouldCreatePolicyWithCorrectDates() {
        when(policyRepository.findByIdempotencyKey(anyString()))
            .thenReturn(Optional.empty());
        doNothing().when(customerValidator).validateExists(any());
        when(policyRepository.existsActivePolicy(any())).thenReturn(false);
        when(policyRepository.save(any(InsurancePolicy.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        InsurancePolicy result = service.execute(customerId, PolicyType.BRONZE, idempotencyKey);

        assertNotNull(result.getStartDate());
        assertNotNull(result.getEndDate());
        assertEquals(1, result.getEndDate().getYear() - result.getStartDate().getYear());
    }
}
