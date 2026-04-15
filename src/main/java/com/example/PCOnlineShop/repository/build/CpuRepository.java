package com.example.PCOnlineShop.repository.build;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PCOnlineShop.model.build.CPU;

@Repository
public interface CpuRepository extends JpaRepository<CPU, Integer> {
    Optional<CPU> findByProduct_ProductId(int id);

    @Query("SELECT DISTINCT c FROM CPU c " +
           "LEFT JOIN FETCH c.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.price <= :maxPrice " +
           "AND (p.performanceScore IS NULL OR p.performanceScore >= :minScore) " +
           "AND p.status = true " +
           "AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING " +
           "AND (p.inventoryQuantity IS NULL OR p.inventoryQuantity > 0) " +
           "ORDER BY COALESCE(p.performanceScore, 50) DESC, p.price ASC")
    List<CPU> findBestCpusByBudgetAndScore(@Param("maxPrice") double maxPrice,
                                            @Param("minScore") int minScore);

    @Query("SELECT DISTINCT c FROM CPU c " +
           "LEFT JOIN FETCH c.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE p.status = true " +
           "AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING")
    List<CPU> findAllWithImages();

    @Query("SELECT DISTINCT c FROM CPU c " +
           "LEFT JOIN FETCH c.product p " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE c.productId = :productId")
    Optional<CPU> findByIdWithImages(@Param("productId") int productId);
}
