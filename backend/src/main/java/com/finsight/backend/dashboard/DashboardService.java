package com.finsight.backend.dashboard;

import com.finsight.backend.dashboard.dto.CategoryBreakdownResponse;
import com.finsight.backend.dashboard.dto.DailyExpenseResponse;
import com.finsight.backend.dashboard.dto.DashboardResponse;
import com.finsight.backend.dashboard.dto.IncomeVsExpenseResponse;
import com.finsight.backend.expense.Expense;
import com.finsight.backend.expense.ExpenseRepository;
import com.finsight.backend.income.Income;
import com.finsight.backend.income.IncomeRepository;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ExpenseRepository expenseRepo;
    private final IncomeRepository incomeRepo;

    public DashboardService(ExpenseRepository expenseRepo, IncomeRepository incomeRepo) {
        this.expenseRepo = expenseRepo;
        this.incomeRepo = incomeRepo;
    }

    public DashboardResponse getSummary(int month, int year) {

        User user = getUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Expense> expenses = expenseRepo.findByUserAndExpenseDateBetween(user, start, end);

        List<Income> incomes = incomeRepo.findByUserAndIncomeDateBetween(user, start, end);

        BigDecimal totalExpense = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : balance.multiply(BigDecimal.valueOf(100))
                        .divide(totalIncome, 2, RoundingMode.HALF_UP)
                        .doubleValue();

        String topCategory = getTopCategory(expenses);

        return new DashboardResponse(
                totalIncome,
                totalExpense,
                balance,
                savingsRate,
                topCategory);
    }

    private String getTopCategory(List<Expense> expenses) {

        if (expenses.isEmpty())
            return "N/A";

        Map<String, BigDecimal> map = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)));

        return map.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public List<DailyExpenseResponse> getDailyExpenses(int month, int year) {

        User user = getUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Expense> expenses = expenseRepo.findByUserAndExpenseDateBetween(user, start, end);

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getExpenseDate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(e -> new DailyExpenseResponse(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DailyExpenseResponse::date))
                .toList();
    }

    public List<CategoryBreakdownResponse> getCategoryBreakdown(int month, int year) {

        User user = getUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Expense> expenses = expenseRepo.findByUserAndExpenseDateBetween(user, start, end);

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(e -> new CategoryBreakdownResponse(e.getKey(), e.getValue()))
                .toList();
    }

    public IncomeVsExpenseResponse getIncomeVsExpense(int month, int year) {

        User user = getUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal income = incomeRepo.findByUserAndIncomeDateBetween(user, start, end)
                .stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = expenseRepo.findByUserAndExpenseDateBetween(user, start, end)
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new IncomeVsExpenseResponse(income, expense);
    }
}