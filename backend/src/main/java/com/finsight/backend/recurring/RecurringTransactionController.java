package com.finsight.backend.recurring;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.recurring.dto.RecurringTransactionRequest;
import com.finsight.backend.recurring.dto.RecurringTransactionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recurring")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringService;

    public RecurringTransactionController(
            RecurringTransactionService recurringService) {
        this.recurringService = recurringService;
    }

    @PostMapping
    public ApiResponse<RecurringTransactionResponse> create(
            @Valid @RequestBody RecurringTransactionRequest request) {
        return ApiResponse.success(
                "Recurring transaction created successfully",
                recurringService.create(request));
    }

    @GetMapping
    public ApiResponse<List<RecurringTransactionResponse>> getAll() {
        return ApiResponse.success(
                "Recurring transactions fetched successfully",
                recurringService.getAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<RecurringTransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RecurringTransactionRequest request) {
        return ApiResponse.success(
                "Recurring transaction updated successfully",
                recurringService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        recurringService.delete(id);

        return ApiResponse.success(
                "Recurring transaction deleted successfully",
                null);
    }

    @PostMapping("/{id}/generate")
    public ApiResponse<String> generate(
            @PathVariable UUID id,
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success(
                recurringService.generate(id, month, year),
                null);
    }
}