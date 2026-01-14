package com.insurance.policy.domain.model;

import java.math.BigDecimal;

public enum PolicyType {
    BRONZE(new BigDecimal("50000.00"), new BigDecimal("150.00")),
    SILVER(new BigDecimal("100000.00"), new BigDecimal("300.00")),
    GOLD(new BigDecimal("200000.00"), new BigDecimal("500.00"));

    private final BigDecimal coverageAmount;
    private final BigDecimal monthlyPremium;

    PolicyType(BigDecimal coverageAmount, BigDecimal monthlyPremium) {
        this.coverageAmount = coverageAmount;
        this.monthlyPremium = monthlyPremium;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public BigDecimal getMonthlyPremium() {
        return monthlyPremium;
    }
}