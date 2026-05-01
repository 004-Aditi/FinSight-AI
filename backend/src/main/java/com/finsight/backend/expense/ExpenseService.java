package com.finsight.backend.expense;

import com.finsight.backend.expense.dto.ExpenseRequest;
import com.finsight.backend.expense.dto.ExpenseResponse;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        User currentUser = getCurrentUser();

        Expense expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setUser(currentUser);
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setMerchant(request.merchant());
        expense.setPaymentMode(request.paymentMode());
        expense.setExpenseDate(request.expenseDate());

        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        User currentUser = getCurrentUser();

        return expenseRepository.findByUserOrderByExpenseDateDesc(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpenseResponse getExpenseById(UUID id) {
        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(UUID id, ExpenseRequest request) {
        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setMerchant(request.merchant());
        expense.setPaymentMode(request.paymentMode());
        expense.setExpenseDate(request.expenseDate());
        expense.setUpdatedAt(LocalDateTime.now());

        Expense updatedExpense = expenseRepository.save(expense);

        return mapToResponse(updatedExpense);
    }

    public void deleteExpense(UUID id) {
        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expenseRepository.delete(expense);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getMerchant(),
                expense.getPaymentMode(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getUpdatedAt());
    }

    public List<ExpenseResponse> getExpensesByDateRange(
            LocalDate startDate,
            LocalDate endDate) {
        User currentUser = getCurrentUser();

        return expenseRepository
                .findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(
                        currentUser,
                        startDate,
                        endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ExpenseResponse> getExpensesByCategory(String category) {
        User currentUser = getCurrentUser();

        return expenseRepository
                .findByUserAndCategoryIgnoreCase(currentUser, category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public double getTotalSpending() {
        User currentUser = getCurrentUser();

        return expenseRepository.findByUserOrderByExpenseDateDesc(currentUser)
                .stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();
    }

    public Map<String, Double> getCategorySummary() {
        User currentUser = getCurrentUser();

        return expenseRepository.findByUserOrderByExpenseDateDesc(currentUser)
                .stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())));
    }
}