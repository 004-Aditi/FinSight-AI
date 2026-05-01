package com.finsight.backend.analytics.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalExpense,
        long transactionCount,
        BigDecimal averageExpense) {
}