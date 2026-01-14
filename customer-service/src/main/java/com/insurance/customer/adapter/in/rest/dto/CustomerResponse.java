package com.insurance.customer.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer response payload")
public class CustomerResponse {

    @Schema(description = "Customer's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Customer's CPF", example = "12345678901")
    private String cpf;

    @Schema(description = "Customer's full name", example = "João Silva Santos")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Customer's birth date", example = "1990-05-15")
    private LocalDate birthDate;

    @Schema(description = "Customer's phone number", example = "11987654321")
    private String phone;

    @Schema(description = "Customer's address")
    private AddressResponse address;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Version for optimistic locking", example = "0")
    private Integer version;
}