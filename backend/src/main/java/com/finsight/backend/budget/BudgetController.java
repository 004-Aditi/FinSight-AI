package com.finsight.backend.budget;

import com.finsight.backend.budget.dto.BudgetRequest;
import com.finsight.backend.budget.dto.BudgetResponse;
import com.finsight.backend.budget.dto.BudgetStatusResponse;
import com.finsight.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ApiResponse<BudgetResponse> createOrUpdateBudget(
            @Valid @RequestBody BudgetRequest request) {
        return ApiResponse.success(
                "Budget saved successfully",
                budgetService.createOrUpdateBudget(request));
    }

    @GetMapping
    public ApiResponse<List<BudgetResponse>> getBudgets(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Budgets fetched successfully",
                budgetService.getBudgets(month, year));
    }

    @GetMapping("/status")
    public ApiResponse<List<BudgetStatusResponse>> getBudgetStatus(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                "Budget status fetched successfully",
                budgetService.getBudgetStatus(month, year));
    }

    @PutMapping("/{id}")
    public ApiResponse<BudgetResponse> updateBudget(
            @PathVariable UUID id,
            @Valid @RequestBody BudgetRequest request) {
        return ApiResponse.success(
                "Budget updated successfully",
                budgetService.updateBudget(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);

        return ApiResponse.success(
                "Budget deleted successfully",
                null);
    }
}