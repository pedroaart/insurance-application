package com.insurance.policy.adapter.in.rest.dto;

import com.insurance.policy.domain.model.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Insurance policy simulation response")
public class SimulationResponse {

    @Schema(description = "Simulation unique identifier")
    private UUID simulationId;

    @Schema(description = "Customer ID")
    private UUID customerId;

    @Schema(description = "Policy type")
    private PolicyType policyType;

    @Schema(description = "Coverage amount", example = "200000.00")
    private BigDecimal coverageAmount;

    @Schema(description = "Monthly premium", example = "500.00")
    private BigDecimal monthlyPremium;

    @Schema(description = "Annual premium", example = "6000.00")
    private BigDecimal annualPremium;

    @Schema(description = "Simulation message")
    private String message;
}