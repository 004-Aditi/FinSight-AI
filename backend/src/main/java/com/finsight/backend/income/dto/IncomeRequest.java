package com.finsight.backend.income.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeRequest(

        @NotNull @Positive BigDecimal amount,

        String source,

        String description,

        @NotNull LocalDate incomeDate) {
}