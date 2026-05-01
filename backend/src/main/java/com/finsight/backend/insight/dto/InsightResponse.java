package com.finsight.backend.insight.dto;

import java.util.List;

public record InsightResponse(
        int month,
        int year,
        List<String> insights) {
}