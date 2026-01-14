package com.insurance.policy.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.insurance.policy.domain.model.PolicyStatus;
import com.insurance.policy.domain.model.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Insurance policy response")
public class PolicyResponse {
    
    @Schema(description = "Policy unique identifier")
    private UUID id;
    
    @Schema(description = "Customer ID")
    private UUID customerId;
    
    @Schema(description = "Policy number")
    private String policyNumber;
    
    @Schema(description = "Policy type")
    private PolicyType policyType;
    
    @Schema(description = "Coverage amount")
    private BigDecimal coverageAmount;
    
    @Schema(description = "Monthly premium")
    private BigDecimal monthlyPremium;
    
    @Schema(description = "Policy status")
    private PolicyStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Policy start date")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Policy end date")
    private LocalDate endDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
