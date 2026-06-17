package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.repository.product.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final String GEAR_PARENT_CATEGORY = "Other";

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getMainCategories() {
        return categoryRepository.findMainCategories();
    }

    public List<Category> getGearCategories() {
        return categoryRepository.findByParentCategoryName(GEAR_PARENT_CATEGORY);
    }

    public Optional<Category> getGearParentCategory() {
        return findByNameIgnoreCase(GEAR_PARENT_CATEGORY);
    }

    public Optional<Category> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoryRepository.findById(id);
    }

    public Category getRequiredCategory(Integer id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public Optional<Category> findByNameIgnoreCase(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return Optional.empty();
        }
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName.trim());
    }
}
