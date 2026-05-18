package com.budget.app.controller;

import com.budget.app.model.Category;
import com.budget.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

// Handles all HTTP requests related to category management
// This is what your "Manage" button on the UI will talk to
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ── GET /api/categories/user/{userId} ─────────────
    // Returns all categories belonging to a user
    // Your UI calls this to populate the category dropdown
    // Example: GET /api/categories/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Category>> getCategoriesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.getCategoriesByUser(userId));
    }

    // ── GET /api/categories/{id} ──────────────────────
    // Returns one specific category by its ID
    // Example: GET /api/categories/3
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/categories/user/{userId} ────────────
    // Creates a new custom category for a user
    // Called when the user clicks "Add" in the Manage Categories modal
    // Request body must include: name
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createCategory(@PathVariable Long userId,
                                             @Valid @RequestBody Category category) {
        try {
            Category created = categoryService.createCategory(userId, category);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/categories/{id} ───────────────────
    // Deletes a custom category
    // Default categories (isDefault = true) cannot be deleted
    // Called when the user clicks the ✕ button in the Manage Categories modal
    // Example: DELETE /api/categories/4
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (RuntimeException e) {
            // This also catches the "cannot delete default category" error
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
