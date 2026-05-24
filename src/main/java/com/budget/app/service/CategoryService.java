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
    
    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    public Category createCategory(Long userId, Category category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        String upperName = category.getName().toUpperCase().trim();
        
        boolean alreadyExists = categoryRepository
            .findByUserId(userId)
            .stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(upperName));

        if (alreadyExists) {
            throw new RuntimeException("Category '" + upperName + "' already exists");
        }

        category.setName(upperName);
        category.setUser(user);
        category.setIsDefault(false);
        return categoryRepository.save(category);
    }
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        
        if (category.getIsDefault()) {
            throw new RuntimeException("Cannot delete default category: " + category.getName());
        }

        categoryRepository.deleteById(id);
    }
    public void seedDefaultCategories(User user) {
        String[] defaults = {"GENERAL", "FOOD", "TRAVEL", "HOUSING", "UTILITIES", "SALARY"};
        for (String name : defaults) {
            Category category = new Category(name, user, true);
            categoryRepository.save(category);
        }
    }
}
