package com.finsight.backend.dashboard;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.dashboard.dto.CategoryBreakdownResponse;
import com.finsight.backend.dashboard.dto.DailyExpenseResponse;
import com.finsight.backend.dashboard.dto.DashboardResponse;
import com.finsight.backend.dashboard.dto.IncomeVsExpenseResponse;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardResponse> getSummary(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Dashboard summary fetched",
                service.getSummary(month, year));
    }

    @GetMapping("/daily-expenses")
    public ApiResponse<List<DailyExpenseResponse>> dailyExpenses(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Daily expenses",
                service.getDailyExpenses(month, year));
    }

    @GetMapping("/category-breakdown")
    public ApiResponse<List<CategoryBreakdownResponse>> categoryBreakdown(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Category breakdown",
                service.getCategoryBreakdown(month, year));
    }

    @GetMapping("/income-vs-expense")
    public ApiResponse<IncomeVsExpenseResponse> incomeVsExpense(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Income vs Expense",
                service.getIncomeVsExpense(month, year));
    }
}