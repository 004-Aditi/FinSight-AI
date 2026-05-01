package com.finsight.backend.expense;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.expense.dto.ExpenseRequest;
import com.finsight.backend.expense.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
}