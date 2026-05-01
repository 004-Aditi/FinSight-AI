package com.finsight.backend.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        String category,
        String description,
        String merchant,
        String paymentMode,
        LocalDate expenseDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}