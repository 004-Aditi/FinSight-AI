package com.finsight.backend.analytics;

import com.finsight.backend.analytics.dto.MonthlySummaryResponse;
import com.finsight.backend.analytics.dto.TopCategoryResponse;
import com.finsight.backend.expense.Expense;
import com.finsight.backend.expense.ExpenseRepository;
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
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;

    public AnalyticsService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public MonthlySummaryResponse getMonthlySummary(int month, int year) {
        List<Expense> expenses = getMonthlyExpenses(month, year);

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = expenses.size();

        BigDecimal average = count == 0
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        return new MonthlySummaryResponse(total, count, average);
    }

    public Map<String, BigDecimal> getCategoryBreakdown(int month, int year) {
        List<Expense> expenses = getMonthlyExpenses(month, year);

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)));
    }

    public TopCategoryResponse getTopCategory(int month, int year) {
        Map<String, BigDecimal> breakdown = getCategoryBreakdown(month, year);

        return breakdown.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(entry -> new TopCategoryResponse(entry.getKey(), entry.getValue()))
                .orElse(new TopCategoryResponse("N/A", BigDecimal.ZERO));
    }

    private List<Expense> getMonthlyExpenses(int month, int year) {
        User currentUser = getCurrentUser();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return expenseRepository.findByUserAndExpenseDateBetween(
                currentUser,
                startDate,
                endDate);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}