package com.insurance.policy.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.policy.adapter.in.rest.controller.InsuranceController;
import com.insurance.policy.adapter.in.rest.dto.ContractRequest;
import com.insurance.policy.adapter.in.rest.dto.SimulationRequest;
import com.insurance.policy.adapter.in.rest.mapper.PolicyMapper;
import com.insurance.policy.application.exception.CustomerNotFoundException;
import com.insurance.policy.application.exception.DuplicateRequestException;
import com.insurance.policy.application.exception.PolicyAlreadyExistsException;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.model.PolicyType;
import com.insurance.policy.domain.model.Simulation;
import com.insurance.policy.domain.port.in.ContractPolicyUseCase;
import com.insurance.policy.domain.port.in.GetPolicyUseCase;
import com.insurance.policy.domain.port.in.SimulatePolicyUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InsuranceController.class)
@DisplayName("Insurance Controller Tests")
class InsuranceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SimulatePolicyUseCase simulatePolicyUseCase;

    @MockBean
    private ContractPolicyUseCase contractPolicyUseCase;

    @MockBean
    private GetPolicyUseCase getPolicyUseCase;

    @MockBean
    private PolicyMapper policyMapper;

    @Test
    @DisplayName("Should simulate policy successfully")
    void shouldSimulatePolicySuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        SimulationRequest request = SimulationRequest.builder()
            .customerId(customerId)
            .policyType(PolicyType.GOLD)
            .build();

        Simulation simulation = Simulation.builder()
            .id(UUID.randomUUID())
            .customerId(customerId)
            .policyType(PolicyType.GOLD)
            .coverageAmount(new BigDecimal("200000.00"))
            .monthlyPremium(new BigDecimal("500.00"))
            .annualPremium(new BigDecimal("6000.00"))
            .simulatedAt(LocalDateTime.now())
            .build();

        when(simulatePolicyUseCase.execute(any(), any())).thenReturn(simulation);
        when(policyMapper.toSimulationResponse(any())).thenCallRealMethod();

        mockMvc.perform(post("/api/v1/insurance/simulate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.policyType").value("GOLD"));

        verify(simulatePolicyUseCase).execute(customerId, PolicyType.GOLD);
    }

    @Test
    @DisplayName("Should return 404 when customer not found in simulation")
    void shouldReturn404WhenCustomerNotFoundInSimulation() throws Exception {
        UUID customerId = UUID.randomUUID();
        SimulationRequest request = SimulationRequest.builder()
            .customerId(customerId)
            .policyType(PolicyType.BRONZE)
            .build();

        when(simulatePolicyUseCase.execute(any(), any()))
            .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(post("/api/v1/insurance/simulate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should contract policy successfully")
    void shouldContractPolicySuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        String idempotencyKey = "contract-123";
        ContractRequest request = ContractRequest.builder()
            .customerId(customerId)
            .policyType(PolicyType.SILVER)
            .idempotencyKey(idempotencyKey)
            .build();

        InsurancePolicy policy = InsurancePolicy.builder()
            .id(UUID.randomUUID())
            .customerId(customerId)
            .policyNumber("POL-2026-ABC123")
            .policyType(PolicyType.SILVER)
            .status(PolicyStatus.ACTIVE)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusYears(1))
            .build();

        when(contractPolicyUseCase.execute(any(), any(), anyString())).thenReturn(policy);
        when(policyMapper.toPolicyResponse(any())).thenCallRealMethod();

        mockMvc.perform(post("/api/v1/insurance/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.policyType").value("SILVER"));
    }

    @Test
    @DisplayName("Should return 409 when duplicate idempotency key")
    void shouldReturn409WhenDuplicateIdempotencyKey() throws Exception {
        ContractRequest request = ContractRequest.builder()
            .customerId(UUID.randomUUID())
            .policyType(PolicyType.GOLD)
            .idempotencyKey("duplicate-key")
            .build();

        when(contractPolicyUseCase.execute(any(), any(), anyString()))
            .thenThrow(new DuplicateRequestException("Already processed", "duplicate-key"));

        mockMvc.perform(post("/api/v1/insurance/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 409 when customer already has active policy")
    void shouldReturn409WhenCustomerAlreadyHasActivePolicy() throws Exception {
        ContractRequest request = ContractRequest.builder()
            .customerId(UUID.randomUUID())
            .policyType(PolicyType.BRONZE)
            .idempotencyKey("key-123")
            .build();

        when(contractPolicyUseCase.execute(any(), any(), anyString()))
            .thenThrow(new PolicyAlreadyExistsException("Already has policy"));

        mockMvc.perform(post("/api/v1/insurance/contract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
