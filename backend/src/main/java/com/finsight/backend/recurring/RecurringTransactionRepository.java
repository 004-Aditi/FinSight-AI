package com.finsight.backend.recurring;

import com.finsight.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurringTransactionRepository
        extends JpaRepository<RecurringTransaction, UUID> {

    List<RecurringTransaction> findByUserOrderByCreatedAtDesc(User user);

    Optional<RecurringTransaction> findByIdAndUser(UUID id, User user);
}