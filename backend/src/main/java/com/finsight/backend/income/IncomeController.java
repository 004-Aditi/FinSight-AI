package com.finsight.backend.income;

import com.finsight.backend.common.response.ApiResponse;
import com.finsight.backend.income.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService service;

    public IncomeController(IncomeService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<IncomeResponse> create(@Valid @RequestBody IncomeRequest req) {
        return ApiResponse.success("Income added", service.create(req));
    }

    @GetMapping
    public ApiResponse<List<IncomeResponse>> getAll() {
        return ApiResponse.success("All incomes", service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<IncomeResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success("Income", service.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<IncomeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody IncomeRequest req) {
        return ApiResponse.success("Updated", service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("Deleted", null);
    }

    @GetMapping("/total")
    public ApiResponse<Double> total(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.success("Total income", service.getTotal(month, year));
    }
}