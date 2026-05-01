package com.finsight.backend.analytics;

import com.finsight.backend.analytics.dto.MonthlySummaryResponse;
import com.finsight.backend.analytics.dto.TopCategoryResponse;
import com.finsight.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/monthly-summary")
    public ApiResponse<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Monthly summary fetched successfully",
                analyticsService.getMonthlySummary(month, year));
    }

    @GetMapping("/category-breakdown")
    public ApiResponse<Map<String, BigDecimal>> getCategoryBreakdown(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Category breakdown fetched successfully",
                analyticsService.getCategoryBreakdown(month, year));
    }

    @GetMapping("/top-category")
    public ApiResponse<TopCategoryResponse> getTopCategory(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Top category fetched successfully",
                analyticsService.getTopCategory(month, year));
    }
}