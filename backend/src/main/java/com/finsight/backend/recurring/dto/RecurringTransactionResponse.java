package com.finsight.backend.recurring.dto;

import com.finsight.backend.recurring.RecurringType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecurringTransactionResponse(
        UUID id,
        RecurringType type,
        BigDecimal amount,
        String categoryOrSource,
        String description,
        String merchant,
        String paymentMode,
        int dayOfMonth,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}