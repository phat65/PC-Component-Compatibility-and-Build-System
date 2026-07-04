package com.example.PCOnlineShop.repository.product;

import com.example.PCOnlineShop.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Get only main component categories (Mainboard, CPU, GPU, Memory, Storage, Case, Power Supply, Cooling, Other/Gear)
     * Excludes sub-categories like Socket types, Form factors, Memory types, etc.
     */
    @Query("SELECT c FROM Category c WHERE c.categoryName IN " +
            "('Mainboard', 'CPU', 'GPU', 'Memory', 'Storage', 'Case', 'Power Supply', 'Cooling', 'Other', 'Gear') " +
            "ORDER BY c.displayOrder")
    List<Category> findMainCategories();

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    @Query("""
        SELECT c FROM Category c
        WHERE c.parent.categoryName = :parentName
        ORDER BY c.displayOrder, c.categoryName
    """)
    List<Category> findByParentCategoryName(@Param("parentName") String parentName);

    @Query("""
        SELECT c FROM Category c
        WHERE c.parent.categoryName IN :parentNames
        ORDER BY c.displayOrder, c.categoryName
    """)
    List<Category> findByParentCategoryNameIn(@Param("parentNames") Collection<String> parentNames);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Category c
        WHERE c.categoryId = :categoryId
          AND LOWER(c.categoryName) IN ('other', 'gear')
    """)
    boolean existsGearParentCategoryId(@Param("categoryId") Integer categoryId);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Category c
        WHERE c.categoryId = :categoryId
          AND c.parent IS NOT NULL
          AND LOWER(c.parent.categoryName) IN ('other', 'gear')
    """)
    boolean existsGearChildCategoryId(@Param("categoryId") Integer categoryId);
}
