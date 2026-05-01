package com.finsight.backend.category;

import com.finsight.backend.category.dto.CategoryRequest;
import com.finsight.backend.category.dto.CategoryResponse;
import com.finsight.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(
                "Category created successfully",
                categoryService.createCategory(request));
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories(
            @RequestParam(required = false) CategoryType type) {
        return ApiResponse.success(
                "Categories fetched successfully",
                categoryService.getCategories(type));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.success(
                "Category updated successfully",
                categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);

        return ApiResponse.success(
                "Category deleted successfully",
                null);
    }
}