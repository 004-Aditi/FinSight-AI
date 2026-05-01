package com.finsight.backend.analytics.dto;

import java.math.BigDecimal;

public record TopCategoryResponse(
        String category,
        BigDecimal amount) {
}