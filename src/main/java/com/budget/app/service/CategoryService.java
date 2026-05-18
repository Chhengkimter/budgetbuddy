package com.budget.app.service;

import com.budget.app.model.Category;
import com.budget.app.model.User;
import com.budget.app.repository.CategoryRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get all categories for a user ─────────────────
    // Returns both default and custom categories
    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    // ── Get a single category by ID ───────────────────
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // ── Create a new category ─────────────────────────
    // Converts name to uppercase for consistency (e.g. "food" → "FOOD")
    // Prevents duplicate names for the same user
    public Category createCategory(Long userId, Category category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Force uppercase so "food" and "FOOD" don't both get created
        String upperName = category.getName().toUpperCase().trim();

        // Check if this user already has a category with this name
        boolean alreadyExists = categoryRepository
            .findByUserId(userId)
            .stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(upperName));

        if (alreadyExists) {
            throw new RuntimeException("Category '" + upperName + "' already exists");
        }

        category.setName(upperName);
        category.setUser(user);
        category.setIsDefault(false); // user-created categories are never default
        return categoryRepository.save(category);
    }

    // ── Delete a category ─────────────────────────────
    // Default categories (isDefault = true) are locked and cannot be deleted
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Block deletion of default categories
        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot delete default category: " + category.getName());
        }

        categoryRepository.deleteById(id);
    }

    // ── Seed default categories for a new user ────────
    // Call this when a new user registers so they start with default categories
    public void seedDefaultCategories(User user) {
        String[] defaults = {"GENERAL", "FOOD", "TRAVEL", "HOUSING", "UTILITIES", "SALARY"};
        for (String name : defaults) {
            Category category = new Category(name, user, true); // isDefault = true
            categoryRepository.save(category);
        }
    }
}
