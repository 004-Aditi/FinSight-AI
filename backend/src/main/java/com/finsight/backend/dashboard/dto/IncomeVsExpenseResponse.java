package com.finsight.backend.dashboard.dto;

import java.math.BigDecimal;

public record IncomeVsExpenseResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense) {
}