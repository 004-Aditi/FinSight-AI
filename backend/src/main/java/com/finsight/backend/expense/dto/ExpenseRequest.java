package com.finsight.backend.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull(message = "Amount is required") @Positive(message = "Amount must be greater than zero") BigDecimal amount,

        @NotBlank(message = "Category is required") String category,

        String description,

        String merchant,

        String paymentMode,

        @NotNull(message = "Expense date is required") LocalDate expenseDate) {
}