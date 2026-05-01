package com.finsight.backend.config;

import com.finsight.backend.category.Category;
import com.finsight.backend.category.CategoryRepository;
import com.finsight.backend.category.CategoryType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {

        // ✅ If defaults already exist, don't insert again
        if (categoryRepository.findAll().stream().anyMatch(Category::isDefault)) {
            return;
        }
        List<Category> defaultCategories = List.of(

                // EXPENSE categories
                create("Food", CategoryType.EXPENSE, "#FF5733", "utensils"),
                create("Travel", CategoryType.EXPENSE, "#3498DB", "plane"),
                create("Shopping", CategoryType.EXPENSE, "#9B59B6", "shopping-bag"),
                create("Bills", CategoryType.EXPENSE, "#E74C3C", "file-invoice"),
                create("Health", CategoryType.EXPENSE, "#2ECC71", "heartbeat"),
                create("Entertainment", CategoryType.EXPENSE, "#F1C40F", "film"),

                // INCOME categories
                create("Salary", CategoryType.INCOME, "#2ECC71", "money-bill"),
                create("Freelance", CategoryType.INCOME, "#1ABC9C", "laptop-code"),
                create("Business", CategoryType.INCOME, "#34495E", "briefcase"),
                create("Investments", CategoryType.INCOME, "#16A085", "chart-line"));

        categoryRepository.saveAll(defaultCategories);

        System.out.println("✅ Default categories initialized");
    }

    private Category create(
            String name,
            CategoryType type,
            String color,
            String icon) {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setType(type);
        category.setColor(color);
        category.setIcon(icon);
        category.setDefault(true); // IMPORTANT
        category.setUser(null); // default categories
        return category;
    }
}