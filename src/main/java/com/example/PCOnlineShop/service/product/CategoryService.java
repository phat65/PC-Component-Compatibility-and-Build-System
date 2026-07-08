package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.repository.product.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final String GEAR_PARENT_CATEGORY = "Gear";
    private static final List<String> GEAR_PARENT_CATEGORIES = List.of("Gear", "Other");

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
        return categoryRepository.findByParentCategoryNameIn(GEAR_PARENT_CATEGORIES);
    }

    public Optional<Category> getGearParentCategory() {
        return findByNameIgnoreCase(GEAR_PARENT_CATEGORY);
    }

    public boolean isGearParentCategory(Integer categoryId) {
        return categoryId != null && categoryRepository.existsGearParentCategoryId(categoryId);
    }

    public boolean isGearChildCategory(Integer categoryId) {
        return categoryId != null && categoryRepository.existsGearChildCategoryId(categoryId);
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
