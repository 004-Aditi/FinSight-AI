package com.finsight.backend.category.dto;

import com.finsight.backend.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(

        @NotBlank(message = "Name is required") String name,

        @NotNull(message = "Type is required") CategoryType type,

        String color,

        String icon) {
}