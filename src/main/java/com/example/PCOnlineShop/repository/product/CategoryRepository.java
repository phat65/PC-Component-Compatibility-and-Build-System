package com.example.PCOnlineShop.repository.product;

import com.example.PCOnlineShop.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Get only main component categories (Mainboard, CPU, GPU, Memory, Storage, Case, Power Supply, Cooling, Other)
     * Excludes sub-categories like Socket types, Form factors, Memory types, etc.
     */
    @Query("SELECT c FROM Category c WHERE c.categoryName IN " +
            "('Mainboard', 'CPU', 'GPU', 'Memory', 'Storage', 'Case', 'Power Supply', 'Cooling', 'Other') " +
            "ORDER BY c.displayOrder")
    List<Category> findMainCategories();

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    @Query("""
        SELECT c FROM Category c
        WHERE c.parent.categoryName = :parentName
        ORDER BY c.displayOrder, c.categoryName
    """)
    List<Category> findByParentCategoryName(String parentName);
}
