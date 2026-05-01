package com.finsight.backend.expense;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.expense.dto.ExpenseRequest;
import com.finsight.backend.expense.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ApiResponse<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request) {
        return ApiResponse.success(
                "Expense created successfully",
                expenseService.createExpense(request));
    }

    @GetMapping
    public ApiResponse<List<ExpenseResponse>> getAllExpenses() {
        return ApiResponse.success(
                "Expenses fetched successfully",
                expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ApiResponse<ExpenseResponse> getExpenseById(
            @PathVariable UUID id) {
        return ApiResponse.success(
                "Expense fetched successfully",
                expenseService.getExpenseById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ExpenseResponse> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request) {
        return ApiResponse.success(
                "Expense updated successfully",
                expenseService.updateExpense(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteExpense(
            @PathVariable UUID id) {
        expenseService.deleteExpense(id);

        return ApiResponse.success(
                "Expense deleted successfully",
                null);
    }

    @GetMapping("/filter")
    public ApiResponse<List<ExpenseResponse>> getByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ApiResponse.success(
                "Filtered by date",
                expenseService.getExpensesByDateRange(startDate, endDate));
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<ExpenseResponse>> getByCategory(
            @PathVariable String category) {
        return ApiResponse.success(
                "Filtered by category",
                expenseService.getExpensesByCategory(category));
    }

    @GetMapping("/total")
    public ApiResponse<Double> getTotal() {
        return ApiResponse.success(
                "Total spending",
                expenseService.getTotalSpending());
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Double>> getSummary() {
        return ApiResponse.success(
                "Category summary",
                expenseService.getCategorySummary());
    }
}