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

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Category>> getCategoriesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.getCategoriesByUser(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
