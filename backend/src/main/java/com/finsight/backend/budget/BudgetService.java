package com.finsight.backend.budget;

import com.finsight.backend.budget.dto.BudgetRequest;
import com.finsight.backend.budget.dto.BudgetResponse;
import com.finsight.backend.budget.dto.BudgetStatusResponse;
import com.finsight.backend.expense.Expense;
import com.finsight.backend.expense.ExpenseRepository;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public BudgetResponse createOrUpdateBudget(BudgetRequest request) {
        User currentUser = getCurrentUser();

        Budget budget = budgetRepository
                .findByUserAndCategoryIgnoreCaseAndMonthAndYear(
                        currentUser,
                        request.category(),
                        request.month(),
                        request.year())
                .orElseGet(() -> {
                    Budget newBudget = new Budget();
                    newBudget.setId(UUID.randomUUID());
                    newBudget.setUser(currentUser);
                    newBudget.setCategory(request.category());
                    newBudget.setMonth(request.month());
                    newBudget.setYear(request.year());
                    return newBudget;
                });

        budget.setAmount(request.amount());
        budget.setUpdatedAt(LocalDateTime.now());

        Budget savedBudget = budgetRepository.save(budget);

        return mapToResponse(savedBudget);
    }

    public List<BudgetResponse> getBudgets(int month, int year) {
        User currentUser = getCurrentUser();

        return budgetRepository.findByUserAndMonthAndYear(currentUser, month, year)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BudgetStatusResponse> getBudgetStatus(int month, int year) {
        User currentUser = getCurrentUser();

        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(
                currentUser,
                month,
                year);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByUserAndExpenseDateBetween(
                currentUser,
                startDate,
                endDate);

        return budgets.stream()
                .map(budget -> calculateBudgetStatus(budget, expenses))
                .toList();
    }

    private BudgetStatusResponse calculateBudgetStatus(
            Budget budget,
            List<Expense> expenses) {
        BigDecimal spentAmount;

        if (budget.getCategory().equalsIgnoreCase("ALL")) {
            spentAmount = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            spentAmount = expenses.stream()
                    .filter(expense -> expense.getCategory().equalsIgnoreCase(budget.getCategory()))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal remainingAmount = budget.getAmount().subtract(spentAmount);

        double usagePercentage = BigDecimal.ZERO.compareTo(budget.getAmount()) == 0
                ? 0.0
                : spentAmount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                        .doubleValue();

        String status = getStatus(usagePercentage);

        return new BudgetStatusResponse(
                budget.getCategory(),
                budget.getAmount(),
                spentAmount,
                remainingAmount,
                usagePercentage,
                status);
    }

    public BudgetResponse updateBudget(UUID id, BudgetRequest request) {
        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to update this budget");
        }

        budget.setCategory(request.category());
        budget.setAmount(request.amount());
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setUpdatedAt(LocalDateTime.now());

        Budget updatedBudget = budgetRepository.save(budget);

        return mapToResponse(updatedBudget);
    }

    public void deleteBudget(UUID id) {
        User currentUser = getCurrentUser();

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    private String getStatus(double usagePercentage) {
        if (usagePercentage >= 100) {
            return "EXCEEDED";
        }

        if (usagePercentage >= 80) {
            return "WARNING";
        }

        return "SAFE";
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getAmount(),
                budget.getMonth(),
                budget.getYear(),
                budget.getCreatedAt(),
                budget.getUpdatedAt());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}