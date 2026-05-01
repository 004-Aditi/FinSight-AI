package com.finsight.backend.budget.dto;

import java.math.BigDecimal;

public record BudgetStatusResponse(
        String category,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double usagePercentage,
        String status) {
}