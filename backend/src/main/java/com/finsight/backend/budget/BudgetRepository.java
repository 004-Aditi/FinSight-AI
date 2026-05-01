package com.finsight.backend.budget;

import com.finsight.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByUserAndMonthAndYear(User user, int month, int year);

    Optional<Budget> findByUserAndCategoryIgnoreCaseAndMonthAndYear(
            User user,
            String category,
            int month,
            int year);
}