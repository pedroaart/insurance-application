package com.insurance.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Address value object representing customer's address.
 */
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

    /**
     * Validates ZIP code format (8 digits)
     */
    public boolean isValidZipCode() {
        return zipCode != null && zipCode.matches("\\d{8}");
    }

    /**
     * Validates state code (2 letters)
     */
    public boolean isValidState() {
        return state != null && state.matches("[A-Z]{2}");
    }

    /**
     * Business rule: Address must have all required fields
     */
    public boolean isComplete() {
        return street != null && !street.isBlank()
            && number != null && !number.isBlank()
            && neighborhood != null && !neighborhood.isBlank()
            && city != null && !city.isBlank()
            && isValidState()
            && isValidZipCode();
    }

    /**
     * Sanitizes ZIP code removing any non-digit characters
     */
    public void sanitizeZipCode() {
        if (zipCode != null) {
            this.zipCode = zipCode.replaceAll("\\D", "");
        }
    }

    /**
     * Formats address as a single line
     */
    public String getFormattedAddress() {
        StringBuilder formatted = new StringBuilder();
        formatted.append(street).append(", ").append(number);
        if (complement != null && !complement.isBlank()) {
            formatted.append(" - ").append(complement);
        }
        formatted.append(", ").append(neighborhood);
        formatted.append(" - ").append(city).append("/").append(state);
        formatted.append(" - CEP: ").append(zipCode);
        return formatted.toString();
    }
}
