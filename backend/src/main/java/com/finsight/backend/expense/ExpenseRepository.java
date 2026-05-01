package com.finsight.backend.expense;

import com.finsight.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserOrderByExpenseDateDesc(User user);

    Optional<Expense> findByIdAndUser(UUID id, User user);

    List<Expense> findByUserAndExpenseDateBetweenOrderByExpenseDateDesc(
            User user,
            LocalDate startDate,
            LocalDate endDate);

    List<Expense> findByUserAndCategoryIgnoreCase(
            User user,
            String category);

    List<Expense> findByUserAndExpenseDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate);

}