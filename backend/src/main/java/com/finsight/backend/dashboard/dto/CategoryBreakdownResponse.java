package com.finsight.backend.dashboard.dto;

import java.math.BigDecimal;

public record CategoryBreakdownResponse(
        String category,
        BigDecimal amount) {
}