package com.insurance.customer.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a customer")
public class CustomerRequest {
    
    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
    @Schema(description = "Customer's CPF (11 digits)", example = "12345678901")
    private String cpf;
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    @Schema(description = "Customer's full name", example = "João Silva Santos")
    private String name;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Customer's birth date", example = "1990-05-15")
    private LocalDate birthDate;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10,11}", message = "Phone must contain 10 or 11 digits")
    @Schema(description = "Customer's phone number (10 or 11 digits)", example = "11987654321")
    private String phone;
    
    @NotNull(message = "Address is required")
    @Valid
    @Schema(description = "Customer's address")
    private AddressRequest address;
}
