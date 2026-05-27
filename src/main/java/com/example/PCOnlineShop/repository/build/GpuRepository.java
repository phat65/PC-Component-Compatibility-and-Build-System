package com.example.PCOnlineShop.repository.build;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PCOnlineShop.model.build.GPU;

@Repository
public interface GpuRepository extends JpaRepository<GPU, Integer> {
    Optional<GPU> findByProduct_ProductId(int id);

    @Query("SELECT DISTINCT g FROM GPU g " +
           "LEFT JOIN FETCH g.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.price <= :maxPrice " +
           "AND (p.performanceScore IS NULL OR p.performanceScore >= :minScore) " +
           "AND p.status = true " +
           "AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING " +
           "AND (p.inventoryQuantity IS NULL OR p.inventoryQuantity > 0) " +
           "ORDER BY COALESCE(p.performanceScore, 50) DESC, p.price ASC")
    List<GPU> findBestGpusByBudgetAndScore(@Param("maxPrice") double maxPrice,
                                            @Param("minScore") int minScore);

    @Query("SELECT DISTINCT g FROM GPU g " +
           "LEFT JOIN FETCH g.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.status = true " +
           "AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING " +
           "AND (p.inventoryQuantity IS NULL OR p.inventoryQuantity > 0)")
    List<GPU> findAllWithImages();

    @Query("SELECT DISTINCT g FROM GPU g " +
           "LEFT JOIN FETCH g.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE g.productId = :productId " +
           "AND p.status = true " +
           "AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING " +
           "AND (p.inventoryQuantity IS NULL OR p.inventoryQuantity > 0)")
    Optional<GPU> findByIdWithImages(@Param("productId") int productId);
}
