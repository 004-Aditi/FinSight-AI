package com.finsight.backend.category.dto;

import com.finsight.backend.category.CategoryType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        CategoryType type,
        String color,
        String icon,
        boolean isDefault,
        LocalDateTime createdAt) {
}