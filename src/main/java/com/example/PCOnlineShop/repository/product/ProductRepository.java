package com.example.PCOnlineShop.repository.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByBrand_BrandId(Integer brandId, Pageable pageable);

    // Find product have category_id in list categories
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c.categoryId = :categoryId")
    Page<Product> findByCategory_CategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);

    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN p.categories c
        WHERE c.categoryId = :categoryId
          AND p.productId != :currentProductId
          AND p.status = true
          AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING
        ORDER BY function('RAND')
    """)
    List<Product> findRandomRelatedProductsByCategory(@Param("categoryId") Integer categoryId,
                                                      @Param("currentProductId") Integer currentProductId,
                                                      Pageable pageable);

    @EntityGraph(attributePaths = "images")
    @Query("SELECT DISTINCT p FROM Product p")
    List<Product> findAllWithImages();

    @EntityGraph(attributePaths = {"images", "brand"})
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN p.categories c
        LEFT JOIN c.parent parent
        WHERE p.status = true
          AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING
          AND (
              LOWER(c.categoryName) = 'other'
              OR LOWER(parent.categoryName) = 'other'
          )
    """)
    List<Product> findSellableOtherProductsWithDetails();

    @EntityGraph(attributePaths = {"images", "brand"})
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN p.categories c
        LEFT JOIN c.parent parent
        WHERE p.productId = :productId
          AND p.status = true
          AND p.lifecycleStatus = com.example.PCOnlineShop.model.product.ProductLifecycleStatus.SELLING
          AND (
              LOWER(c.categoryName) = 'other'
              OR LOWER(parent.categoryName) = 'other'
          )
    """)
    Optional<Product> findSellableOtherProductByIdWithDetails(@Param("productId") Integer productId);

    @EntityGraph(attributePaths = "images")
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Integer productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByProductIdForUpdate(@Param("productId") Integer productId);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN p.brand b
        LEFT JOIN p.categories c
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:brandId IS NULL OR b.brandId = :brandId)
          AND (:categoryId IS NULL OR c.categoryId = :categoryId)
    """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("brandId") Integer brandId,
                         @Param("categoryId") Integer categoryId,
                         Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    boolean existsByProductNameAndStatusTrue(String productName);

    boolean existsByProductName(String productName);

    @EntityGraph(attributePaths = {"brand", "categories", "images"})
    Optional<Product> findWithDetailsByProductId(int productId);

    long countByBrand_BrandId(Integer brandId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Product p SET p.brand.brandId = :targetId WHERE p.brand.brandId = :sourceId")
    void reassignBrandByIds(@Param("sourceId") Integer sourceId,
                            @Param("targetId") Integer targetId);

    @EntityGraph(attributePaths = "images")
    Page<Product> findByStatusTrueAndLifecycleStatusOrderByProductIdDesc(ProductLifecycleStatus lifecycleStatus,
                                                                         Pageable pageable);

    // Find products with a specific category and that are currently active.
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c = :category AND p.status = true AND p.lifecycleStatus = :lifecycleStatus")
    @EntityGraph(attributePaths = "images")
    List<Product> findByCategoryAndStatusTrueAndLifecycleStatus(@Param("category") Category category,
                                                                @Param("lifecycleStatus") ProductLifecycleStatus lifecycleStatus);

    @EntityGraph(attributePaths = "images")
    List<Product> findByBrandAndStatusTrueAndLifecycleStatus(Brand brand, ProductLifecycleStatus lifecycleStatus);

    @EntityGraph(attributePaths = "images")
    List<Product> findByStatusTrueAndLifecycleStatus(ProductLifecycleStatus lifecycleStatus);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN p.categories c
        WHERE p.status = true
          AND p.lifecycleStatus = :lifecycleStatus
          AND (:categoryId IS NULL OR c.categoryId = :categoryId)
          AND (:brandId IS NULL OR p.brand.brandId = :brandId)
        """)
    Page<Product> searchProducts(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("lifecycleStatus") ProductLifecycleStatus lifecycleStatus,
            Pageable pageable
    );

    @Query(value = """
            SELECT DISTINCT p.* FROM product p
            LEFT JOIN product_category pc ON p.product_id = pc.product_id
            WHERE (:categoryId IS NULL OR pc.category_id = :categoryId)
              AND (:brandId IS NULL OR p.brand_id = :brandId)
              AND (:keyword IS NULL OR LOWER(p.product_name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND p.status = TRUE
              AND p.lifecycle_status = :lifecycleStatus
            """,
            countQuery = """
            SELECT COUNT(DISTINCT p.product_id) FROM product p
            LEFT JOIN product_category pc ON p.product_id = pc.product_id
            WHERE (:categoryId IS NULL OR pc.category_id = :categoryId)
              AND (:brandId IS NULL OR p.brand_id = :brandId)
              AND (:keyword IS NULL OR LOWER(p.product_name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND p.status = TRUE
              AND p.lifecycle_status = :lifecycleStatus
            """,
            nativeQuery = true)
    Page<Product> searchProducts(
            @Param("categoryId") Integer categoryId,
            @Param("brandId") Integer brandId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("keyword") String keyword,
            @Param("lifecycleStatus") String lifecycleStatus,
            Pageable pageable
    );

    boolean existsByProductNameIgnoreCaseAndLifecycleStatusNot(String productName, ProductLifecycleStatus lifecycleStatus);

    long countByInventoryQuantityGreaterThan(Integer inventoryQuantity);

    long countByInventoryQuantityLessThanEqual(Integer inventoryQuantity);

    @EntityGraph(attributePaths = "images")
    @Query("""
        SELECT DISTINCT p FROM Product p
        WHERE p.productId = :productId
          AND p.status = true
          AND p.lifecycleStatus = :lifecycleStatus
    """)
    Optional<Product> findVisibleByProductIdAndLifecycleStatus(@Param("productId") Integer productId,
                                                               @Param("lifecycleStatus") ProductLifecycleStatus lifecycleStatus);

}
