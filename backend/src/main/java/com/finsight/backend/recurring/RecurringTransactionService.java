package com.finsight.backend.recurring;

import com.finsight.backend.expense.Expense;
import com.finsight.backend.expense.ExpenseRepository;
import com.finsight.backend.income.Income;
import com.finsight.backend.income.IncomeRepository;
import com.finsight.backend.recurring.dto.RecurringTransactionRequest;
import com.finsight.backend.recurring.dto.RecurringTransactionResponse;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public RecurringTransactionService(
            RecurringTransactionRepository recurringRepository,
            ExpenseRepository expenseRepository,
            IncomeRepository incomeRepository) {
        this.recurringRepository = recurringRepository;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public RecurringTransactionResponse create(RecurringTransactionRequest request) {
        User user = getCurrentUser();

        RecurringTransaction recurring = new RecurringTransaction();
        recurring.setId(UUID.randomUUID());
        recurring.setUser(user);
        applyRequest(recurring, request);

        return mapToResponse(recurringRepository.save(recurring));
    }

    public List<RecurringTransactionResponse> getAll() {
        User user = getCurrentUser();

        return recurringRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public RecurringTransactionResponse update(UUID id, RecurringTransactionRequest request) {
        User user = getCurrentUser();

        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Recurring transaction not found"));

        applyRequest(recurring, request);
        recurring.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(recurringRepository.save(recurring));
    }

    public void delete(UUID id) {
        User user = getCurrentUser();

        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Recurring transaction not found"));

        recurringRepository.delete(recurring);
    }

    public String generate(UUID id, int month, int year) {
        User user = getCurrentUser();

        RecurringTransaction recurring = recurringRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Recurring transaction not found"));

        if (!recurring.isActive()) {
            throw new RuntimeException("Recurring transaction is inactive");
        }

        LocalDate date = LocalDate.of(year, month, recurring.getDayOfMonth());

        if (recurring.getType() == RecurringType.EXPENSE) {
            Expense expense = new Expense();
            expense.setId(UUID.randomUUID());
            expense.setUser(user);
            expense.setAmount(recurring.getAmount());
            expense.setCategory(recurring.getCategoryOrSource());
            expense.setDescription(recurring.getDescription());
            expense.setMerchant(recurring.getMerchant());
            expense.setPaymentMode(recurring.getPaymentMode());
            expense.setExpenseDate(date);

            expenseRepository.save(expense);

            return "Expense generated successfully";
        }

        Income income = new Income();
        income.setId(UUID.randomUUID());
        income.setUser(user);
        income.setAmount(recurring.getAmount());
        income.setSource(recurring.getCategoryOrSource());
        income.setDescription(recurring.getDescription());
        income.setIncomeDate(date);

        incomeRepository.save(income);

        return "Income generated successfully";
    }

    private void applyRequest(
            RecurringTransaction recurring,
            RecurringTransactionRequest request) {
        recurring.setType(request.type());
        recurring.setAmount(request.amount());
        recurring.setCategoryOrSource(request.categoryOrSource());
        recurring.setDescription(request.description());
        recurring.setMerchant(request.merchant());
        recurring.setPaymentMode(request.paymentMode());
        recurring.setDayOfMonth(request.dayOfMonth());
        recurring.setActive(request.active());
    }

    private RecurringTransactionResponse mapToResponse(RecurringTransaction recurring) {
        return new RecurringTransactionResponse(
                recurring.getId(),
                recurring.getType(),
                recurring.getAmount(),
                recurring.getCategoryOrSource(),
                recurring.getDescription(),
                recurring.getMerchant(),
                recurring.getPaymentMode(),
                recurring.getDayOfMonth(),
                recurring.isActive(),
                recurring.getCreatedAt(),
                recurring.getUpdatedAt());
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}