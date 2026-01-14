package com.insurance.policy.adapter.in.rest.dto;

import com.insurance.policy.domain.model.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to simulate an insurance policy")
public class SimulationRequest {
    
    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID customerId;
    
    @NotNull(message = "Policy type is required")
    @Schema(description = "Type of insurance policy (BRONZE, SILVER, or GOLD)", example = "GOLD", required = true)
    private PolicyType policyType;
}
