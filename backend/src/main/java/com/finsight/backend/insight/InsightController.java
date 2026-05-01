package com.finsight.backend.insight;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.insight.dto.InsightResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    @GetMapping("/monthly")
    public ApiResponse<InsightResponse> getMonthlyInsights(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Monthly insights generated successfully",
                insightService.getMonthlyInsights(month, year));
    }
}