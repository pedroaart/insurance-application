package com.insurance.policy.adapter.in.rest.controller;

import com.insurance.policy.adapter.in.rest.dto.ContractRequest;
import com.insurance.policy.adapter.in.rest.dto.PolicyResponse;
import com.insurance.policy.adapter.in.rest.dto.SimulationRequest;
import com.insurance.policy.adapter.in.rest.dto.SimulationResponse;
import com.insurance.policy.adapter.in.rest.mapper.PolicyMapper;
import com.insurance.policy.domain.model.InsurancePolicy;
import com.insurance.policy.domain.model.Simulation;
import com.insurance.policy.domain.port.in.ContractPolicyUseCase;
import com.insurance.policy.domain.port.in.GetPolicyUseCase;
import com.insurance.policy.domain.port.in.SimulatePolicyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/insurance")
@RequiredArgsConstructor
@Tag(name = "Insurance", description = "Insurance policy management endpoints")
public class InsuranceController {
    
    private final SimulatePolicyUseCase simulatePolicyUseCase;
    private final ContractPolicyUseCase contractPolicyUseCase;
    private final GetPolicyUseCase getPolicyUseCase;
    private final PolicyMapper mapper;

    @Operation(summary = "Simulate insurance policy", 
               description = "Simulates an insurance policy for a customer without creating a contract")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Simulation created successfully",
                    content = @Content(schema = @Schema(implementation = SimulationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "503", description = "Customer service unavailable")
    })
    @PostMapping("/simulate")
    public ResponseEntity<SimulationResponse> simulate(
            @Valid @RequestBody SimulationRequest request) {
        
        log.info("Received simulation request for customer: {} with type: {}", 
                 request.getCustomerId(), request.getPolicyType());
        
        Simulation simulation = simulatePolicyUseCase.execute(
            request.getCustomerId(), 
            request.getPolicyType()
        );
        
        SimulationResponse response = mapper.toSimulationResponse(simulation);
        
        log.info("Simulation completed successfully: {}", simulation.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Contract insurance policy", 
               description = "Creates an insurance policy contract for a customer. This operation is idempotent.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Policy contracted successfully",
                    content = @Content(schema = @Schema(implementation = PolicyResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Customer not found"),
        @ApiResponse(responseCode = "409", description = "Customer already has an active policy or duplicate idempotency key"),
        @ApiResponse(responseCode = "503", description = "Customer service unavailable")
    })
    @PostMapping("/contract")
    public ResponseEntity<PolicyResponse> contract(
            @Valid @RequestBody ContractRequest request) {
        
        log.info("Received contract request for customer: {} with idempotency key: {}", 
                 request.getCustomerId(), request.getIdempotencyKey());
        
        InsurancePolicy policy = contractPolicyUseCase.execute(
            request.getCustomerId(),
            request.getPolicyType(),
            request.getIdempotencyKey()
        );
        
        PolicyResponse response = mapper.toPolicyResponse(policy);
        
        log.info("Policy contracted successfully: {}", policy.getPolicyNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get policy by ID", description = "Retrieves a specific insurance policy by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policy found",
                    content = @Content(schema = @Schema(implementation = PolicyResponse.class))),
        @ApiResponse(responseCode = "404", description = "Policy not found")
    })
    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> getPolicy(
            @Parameter(description = "Policy unique identifier") 
            @PathVariable UUID policyId) {
        
        log.info("Received request to get policy: {}", policyId);
        
        InsurancePolicy policy = getPolicyUseCase.findById(policyId);
        PolicyResponse response = mapper.toPolicyResponse(policy);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get policies by customer", 
               description = "Retrieves all insurance policies for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Policies retrieved successfully"),
        @ApiResponse(responseCode = "200", description = "No policies found (empty list)")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PolicyResponse>> getPoliciesByCustomer(
            @Parameter(description = "Customer unique identifier") 
            @PathVariable UUID customerId) {
        
        log.info("Received request to get policies for customer: {}", customerId);
        
        List<InsurancePolicy> policies = getPolicyUseCase.findByCustomerId(customerId);
        List<PolicyResponse> response = policies.stream()
            .map(mapper::toPolicyResponse)
            .collect(Collectors.toList());
        
        log.info("Found {} policies for customer: {}", response.size(), customerId);
        return ResponseEntity.ok(response);
    }
}
