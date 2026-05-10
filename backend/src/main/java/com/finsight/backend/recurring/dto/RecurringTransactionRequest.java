package com.finsight.backend.recurring.dto;

import com.finsight.backend.recurring.RecurringType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RecurringTransactionRequest(
        @NotNull(message = "Type is required") RecurringType type,

        @NotNull(message = "Amount is required") @Positive(message = "Amount must be greater than zero") BigDecimal amount,

        @NotBlank(message = "Category or source is required") String categoryOrSource,

        String description,

        String merchant,

        String paymentMode,

        @Min(value = 1, message = "Day must be between 1 and 28") @Max(value = 28, message = "Day must be between 1 and 28") int dayOfMonth,

        boolean active) {
}