package com.insurance.customer.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address information")
public class AddressRequest {
    
    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @Schema(description = "Street name", example = "Rua das Flores")
    private String street;
    
    @NotBlank(message = "Number is required")
    @Size(max = 20, message = "Number must not exceed 20 characters")
    @Schema(description = "Street number", example = "123")
    private String number;
    
    @Size(max = 255, message = "Complement must not exceed 255 characters")
    @Schema(description = "Address complement (optional)", example = "Apto 45")
    private String complement;
    
    @NotBlank(message = "Neighborhood is required")
    @Size(max = 100, message = "Neighborhood must not exceed 100 characters")
    @Schema(description = "Neighborhood", example = "Jardim Paulista")
    private String neighborhood;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "São Paulo")
    private String city;
    
    @NotBlank(message = "State is required")
    @Pattern(regexp = "[A-Z]{2}", message = "State must be 2 uppercase letters")
    @Schema(description = "State code (2 letters)", example = "SP")
    private String state;
    
    @NotBlank(message = "ZIP code is required")
    @Pattern(regexp = "\\d{8}", message = "ZIP code must contain exactly 8 digits")
    @Schema(description = "ZIP code (8 digits)", example = "01310100")
    private String zipCode;
}
