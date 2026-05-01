package com.finsight.backend.category;

import com.finsight.backend.category.dto.CategoryRequest;
import com.finsight.backend.category.dto.CategoryResponse;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User user = getCurrentUser();

        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setUser(user);
        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
        category.setIcon(request.icon());
        category.setDefault(false);

        Category saved = categoryRepository.save(category);

        return mapToResponse(saved);
    }

    public List<CategoryResponse> getCategories(CategoryType type) {
        User user = getCurrentUser();

        List<Category> categories;

        if (type != null) {
            categories = categoryRepository.findByUserOrIsDefaultTrueAndType(user, type);
        } else {
            categories = categoryRepository.findByUserOrIsDefaultTrue(user);
        }

        return categories.stream().map(this::mapToResponse).toList();
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.isDefault()) {
            throw new RuntimeException("Default category cannot be updated");
        }

        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());
        category.setIcon(request.icon());
        category.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.isDefault()) {
            throw new RuntimeException("Default category cannot be deleted");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor(),
                category.getIcon(),
                category.isDefault(),
                category.getCreatedAt());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}