package com.finsight.backend.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyExpenseResponse(
        LocalDate date,
        BigDecimal amount) {
}