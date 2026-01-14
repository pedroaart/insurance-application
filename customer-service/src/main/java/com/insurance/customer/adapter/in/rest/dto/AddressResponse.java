package com.insurance.customer.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address response payload")
public class AddressResponse {

    @Schema(description = "Address unique identifier")
    private UUID id;

    @Schema(description = "Street name", example = "Rua das Flores")
    private String street;

    @Schema(description = "Street number", example = "123")
    private String number;

    @Schema(description = "Address complement", example = "Apto 45")
    private String complement;

    @Schema(description = "Neighborhood", example = "Jardim Paulista")
    private String neighborhood;

    @Schema(description = "City", example = "São Paulo")
    private String city;

    @Schema(description = "State code", example = "SP")
    private String state;

    @Schema(description = "ZIP code", example = "01310100")
    private String zipCode;
}