package com.finsight.backend.income.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeResponse(
        UUID id,
        BigDecimal amount,
        String source,
        String description,
        LocalDate incomeDate) {
}