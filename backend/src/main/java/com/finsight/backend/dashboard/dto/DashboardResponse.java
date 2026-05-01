package com.finsight.backend.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        double savingsRate,
        String topExpenseCategory) {
}