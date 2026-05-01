package com.finsight.backend.category;

import com.finsight.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

        List<Category> findByUserOrIsDefaultTrue(User user);

        List<Category> findByUserOrIsDefaultTrueAndType(User user, CategoryType type);
}