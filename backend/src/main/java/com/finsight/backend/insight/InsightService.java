package com.finsight.backend.insight;

import com.finsight.backend.budget.Budget;
import com.finsight.backend.budget.BudgetRepository;
import com.finsight.backend.expense.Expense;
import com.finsight.backend.expense.ExpenseRepository;
import com.finsight.backend.insight.dto.InsightResponse;
import com.finsight.backend.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final AiInsightService aiInsightService;

    public InsightService(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository,
            AiInsightService aiInsightService) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.aiInsightService = aiInsightService;
    }

    public InsightResponse getMonthlyInsights(int month, int year) {

        User currentUser = getCurrentUser();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> currentMonthExpenses = expenseRepository.findByUserAndExpenseDateBetween(
                currentUser,
                startDate,
                endDate);

        List<String> insights = new ArrayList<>();

        // ✅ If no data, return early (no AI call needed)
        if (currentMonthExpenses.isEmpty()) {
            insights.add("No expenses found for this month. Start tracking your spending to get insights.");
            return new InsightResponse(month, year, insights);
        }

        // Build rule-based insights
        addTotalSpendingInsight(insights, currentMonthExpenses);
        addTopCategoryInsight(insights, currentMonthExpenses);
        addBudgetInsights(insights, currentUser, currentMonthExpenses, month, year);
        addMonthComparisonInsight(insights, currentUser, currentMonthExpenses, month, year);
        addTransactionCountInsight(insights, currentMonthExpenses);

        // ✅ AI Enhancement with fallback
        try {
            String aiResponse = aiInsightService.enhanceInsights(insights);

            return new InsightResponse(
                    month,
                    year,
                    List.of(aiResponse));

        } catch (Exception e) {
            // fallback to rule-based insights if AI fails
            return new InsightResponse(month, year, insights);
        }
    }

    // ------------------ INSIGHT BUILDERS ------------------

    private void addTotalSpendingInsight(
            List<String> insights,
            List<Expense> expenses) {
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        insights.add("Your total spending this month is ₹" + total + ".");
    }

    private void addTopCategoryInsight(
            List<String> insights,
            List<Expense> expenses) {
        Map<String, BigDecimal> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)));

        Map.Entry<String, BigDecimal> topCategory = categoryTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (topCategory != null) {
            insights.add(
                    "Your highest spending category is "
                            + topCategory.getKey()
                            + " with ₹"
                            + topCategory.getValue()
                            + " spent.");
        }
    }

    private void addBudgetInsights(
            List<String> insights,
            User user,
            List<Expense> expenses,
            int month,
            int year) {
        List<Budget> budgets = budgetRepository.findByUserAndMonthAndYear(
                user,
                month,
                year);

        if (budgets.isEmpty()) {
            insights.add("You have not set any budget for this month. Setting budgets can help control spending.");
            return;
        }

        for (Budget budget : budgets) {

            BigDecimal spentAmount = calculateSpentForBudget(budget, expenses);

            double usagePercentage = budget.getAmount().compareTo(BigDecimal.ZERO) == 0
                    ? 0.0
                    : spentAmount
                            .multiply(BigDecimal.valueOf(100))
                            .divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                            .doubleValue();

            if (usagePercentage >= 100) {
                insights.add(
                        "You have exceeded your "
                                + budget.getCategory()
                                + " budget. Spent ₹"
                                + spentAmount
                                + " against ₹"
                                + budget.getAmount()
                                + ".");
            } else if (usagePercentage >= 80) {
                insights.add(
                        "You have used "
                                + usagePercentage
                                + "% of your "
                                + budget.getCategory()
                                + " budget.");
            } else {
                insights.add(
                        "Your "
                                + budget.getCategory()
                                + " budget usage is healthy at "
                                + usagePercentage
                                + "%.");
            }
        }
    }

    private BigDecimal calculateSpentForBudget(
            Budget budget,
            List<Expense> expenses) {
        if (budget.getCategory().equalsIgnoreCase("ALL")) {
            return expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(budget.getCategory()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void addMonthComparisonInsight(
            List<String> insights,
            User user,
            List<Expense> currentMonthExpenses,
            int month,
            int year) {
        LocalDate currentStart = LocalDate.of(year, month, 1);
        LocalDate previousStart = currentStart.minusMonths(1);
        LocalDate previousEnd = previousStart.withDayOfMonth(previousStart.lengthOfMonth());

        List<Expense> previousMonthExpenses = expenseRepository.findByUserAndExpenseDateBetween(
                user,
                previousStart,
                previousEnd);

        if (previousMonthExpenses.isEmpty()) {
            insights.add("No previous month data available for comparison.");
            return;
        }

        BigDecimal currentTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousTotal = previousMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (previousTotal.compareTo(BigDecimal.ZERO) == 0)
            return;

        BigDecimal change = currentTotal.subtract(previousTotal);

        BigDecimal percentage = change
                .multiply(BigDecimal.valueOf(100))
                .divide(previousTotal, 2, RoundingMode.HALF_UP);

        if (percentage.compareTo(BigDecimal.ZERO) > 0) {
            insights.add("Your spending increased by " + percentage + "% compared to last month.");
        } else if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            insights.add("Great job! Your spending decreased by " + percentage.abs() + "% compared to last month.");
        } else {
            insights.add("Your spending is the same as last month.");
        }
    }

    private void addTransactionCountInsight(
            List<String> insights,
            List<Expense> expenses) {
        int count = expenses.size();

        if (count >= 20) {
            insights.add("You made " + count + " transactions. Frequent small expenses may be adding up.");
        } else {
            insights.add("You made " + count + " transactions this month.");
        }
    }

    // ------------------ UTIL ------------------

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}