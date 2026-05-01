package com.finsight.backend.budget.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BudgetRequest(
        @NotBlank(message = "Category is required") String category,

        @NotNull(message = "Budget amount is required") @Positive(message = "Budget amount must be greater than zero") BigDecimal amount,

        @Min(value = 1, message = "Month must be between 1 and 12") @Max(value = 12, message = "Month must be between 1 and 12") int month,

        @Min(value = 2000, message = "Year must be valid") int year) {
}