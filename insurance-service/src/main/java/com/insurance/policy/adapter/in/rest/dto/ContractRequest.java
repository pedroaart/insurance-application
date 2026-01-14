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
@Schema(description = "Request to contract an insurance policy")
public class ContractRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer's unique identifier")
    private UUID customerId;

    @NotNull(message = "Policy type is required")
    @Schema(description = "Type of insurance policy")
    private PolicyType policyType;
}