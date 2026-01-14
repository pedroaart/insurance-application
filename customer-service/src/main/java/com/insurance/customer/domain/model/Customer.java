package com.insurance.customer.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customer domain entity representing a client in the insurance system.
 * This is the core business object - framework-agnostic and contains business rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    private UUID id;
    private String cpf;
    private String name;
    private LocalDate birthDate;
    private String phone;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    /**
     * Validates CPF format and checksum
     */
    public boolean isValidCpf() {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d{11}")) {
            return false;
        }
        
        // Check for known invalid CPFs (all same digits)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        return validateCpfChecksum();
    }

    private boolean validateCpfChecksum() {
        try {
            // First digit verification
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;
            
            if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }
            
            // Second digit verification
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;
            
            return secondDigit == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if customer is of legal age (18 years old)
     */
    public boolean isLegalAge() {
        if (birthDate == null) {
            return false;
        }
        return LocalDate.now().minusYears(18).isAfter(birthDate) 
            || LocalDate.now().minusYears(18).isEqual(birthDate);
    }

    /**
     * Business rule: Customer must have valid CPF and be of legal age
     */
    public boolean canPurchaseInsurance() {
        return isValidCpf() && isLegalAge() && address != null;
    }

    /**
     * Sanitizes CPF removing any non-digit characters
     */
    public void sanitizeCpf() {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("\\D", "");
        }
    }
}
