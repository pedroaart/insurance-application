package com.insurance.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    private UUID id;
    private UUID customerId;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isValidZipCode() {
        return zipCode != null && zipCode.matches("\\d{8}");
    }

    public boolean isValidState() {
        return state != null && state.matches("[A-Z]{2}");
    }

    public boolean isComplete() {
        return street != null && !street.isBlank()
            && number != null && !number.isBlank()
            && neighborhood != null && !neighborhood.isBlank()
            && city != null && !city.isBlank()
            && isValidState()
            && isValidZipCode();
    }

    public void sanitizeZipCode() {
        if (zipCode != null) {
            this.zipCode = zipCode.replaceAll("\\D", "");
        }
    }
}
