package com.finsight.backend.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        String category,
        BigDecimal amount,
        int month,
        int year,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}