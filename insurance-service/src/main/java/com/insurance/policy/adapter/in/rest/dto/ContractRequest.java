package com.insurance.policy.adapter.in.rest.dto;

import com.insurance.policy.domain.model.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request to contract an insurance policy")
public class ContractRequest {
    
    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer's unique identifier", required = true)
    private UUID customerId;
    
    @NotNull(message = "Policy type is required")
    @Schema(description = "Type of insurance policy", required = true)
    private PolicyType policyType;
    
    @NotBlank(message = "Idempotency key is required")
    @Schema(description = "Idempotency key to prevent duplicate contracts", 
            example = "contract-123e4567-e89b-12d3-a456-426614174000", 
            required = true)
    private String idempotencyKey;
}
