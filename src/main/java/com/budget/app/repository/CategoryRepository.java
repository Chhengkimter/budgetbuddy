package com.budget.app.repository;

import com.budget.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Get all categories for a specific user (default + custom)
    // SQL: SELECT * FROM categories WHERE user_id = ?
    List<Category> findByUserId(Long userId);

    // Get only the default categories for a user
    // SQL: SELECT * FROM categories WHERE user_id = ? AND is_default = true
    List<Category> findByUserIdAndIsDefaultTrue(Long userId);
}
