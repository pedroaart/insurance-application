package com.insurance.policy.domain.model;

import java.math.BigDecimal;

public enum PolicyType {
    BRONZE(new BigDecimal("50000.00"), new BigDecimal("150.00"), "Basic coverage"),
    SILVER(new BigDecimal("100000.00"), new BigDecimal("300.00"), "Standard coverage"),
    GOLD(new BigDecimal("200000.00"), new BigDecimal("500.00"), "Premium coverage");

    private final BigDecimal coverageAmount;
    private final BigDecimal monthlyPremium;
    private final String description;

    PolicyType(BigDecimal coverageAmount, BigDecimal monthlyPremium, String description) {
        this.coverageAmount = coverageAmount;
        this.monthlyPremium = monthlyPremium;
        this.description = description;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public BigDecimal getMonthlyPremium() {
        return monthlyPremium;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAnnualPremium() {
        return monthlyPremium.multiply(new BigDecimal("12"));
    }
}