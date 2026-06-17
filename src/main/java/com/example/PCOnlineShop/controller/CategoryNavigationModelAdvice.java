package com.example.PCOnlineShop.controller;

import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CategoryNavigationModelAdvice {

    private final CategoryService categoryService;

    @ModelAttribute("gearCategories")
    public List<Category> gearCategories() {
        try {
            return categoryService.getGearCategories();
        } catch (RuntimeException e) {
            log.warn("Unable to load gear categories for navigation", e);
            return List.of();
        }
    }

    @ModelAttribute("gearParentCategory")
    public Category gearParentCategory() {
        try {
            return categoryService.getGearParentCategory().orElse(null);
        } catch (RuntimeException e) {
            log.warn("Unable to load gear parent category for navigation", e);
            return null;
        }
    }
}
