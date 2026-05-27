package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;
import com.example.PCOnlineShop.repository.order.OrderDetailRepository;
import com.example.PCOnlineShop.repository.product.BrandRepository;
import com.example.PCOnlineShop.repository.product.CategoryRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private static final ProductLifecycleStatus PUBLIC_CATALOG_STATUS = ProductLifecycleStatus.SELLING;
    private static final int FEATURED_PRODUCTS_LIMIT = 8;
    private static final int RELATED_PRODUCTS_LIMIT = 8;
    private static final List<String> ACTIVE_ORDER_STATUSES = List.of(
            "Pending",
            "Processing",
            "Delivering",
            "Ready to Ship"
    );

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository,
                          BrandRepository brandRepository,
                          CategoryRepository categoryRepository,
                          OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Product> getProductsForManagement() {
        return productRepository.findAllWithImages();
    }

    public Optional<Product> findProductById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return productRepository.findById(id);
    }

    public Product getRequiredProduct(Integer id) {
        return findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product saveProduct(Product product) {
        prepareCatalogState(product);
        return productRepository.save(product);
    }

    public List<Product> getFeaturedStorefrontProducts() {
        return productRepository.findByStatusTrueAndLifecycleStatusOrderByProductIdDesc(
                PUBLIC_CATALOG_STATUS,
                PageRequest.of(0, FEATURED_PRODUCTS_LIMIT)
        ).getContent();
    }

    public List<Product> getStorefrontProductsByCategory(Integer categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category
                .map(value -> productRepository.findByCategoryAndStatusTrueAndLifecycleStatus(value, PUBLIC_CATALOG_STATUS))
                .orElse(List.of());
    }

    public List<Product> getStorefrontProductsByBrand(Integer brandId) {
        if (brandId == null) {
            return List.of();
        }
        Optional<Brand> brand = brandRepository.findById(brandId);
        return brand
                .map(value -> productRepository.findByBrandAndStatusTrueAndLifecycleStatus(value, PUBLIC_CATALOG_STATUS))
                .orElse(List.of());
    }

    public List<Product> getRelatedStorefrontProducts(Product product) {
        if (product == null || product.getCategories() == null || product.getCategories().isEmpty()) {
            return List.of();
        }

        Category primaryCategory = product.getCategories().getFirst();
        return productRepository.findRandomRelatedProductsByCategory(
                primaryCategory.getCategoryId(),
                product.getProductId(),
                PageRequest.of(0, RELATED_PRODUCTS_LIMIT)
        );
    }

    public boolean existsNonDiscontinuedProductName(String productName) {
        String normalizedName = normalizeKeyword(productName);
        if (normalizedName == null) {
            return false;
        }
        return productRepository.existsByProductNameIgnoreCaseAndLifecycleStatusNot(
                normalizedName,
                ProductLifecycleStatus.DISCONTINUED
        );
    }

    public boolean hasActiveOrderReferences(int productId) {
        return orderDetailRepository.existsByProduct_ProductIdAndOrder_StatusIn(productId, ACTIVE_ORDER_STATUSES);
    }

    public Page<Product> searchVisibleSellingProducts(Integer categoryId, Integer brandId, Pageable pageable) {
        return productRepository.searchProducts(categoryId, brandId, PUBLIC_CATALOG_STATUS, pageable);
    }

    public Page<Product> searchVisibleSellingProducts(Integer categoryId,
                                                      Integer brandId,
                                                      Double minPrice,
                                                      Double maxPrice,
                                                      String keyword,
                                                      Pageable pageable) {
        return productRepository.searchProducts(
                categoryId,
                brandId,
                minPrice,
                maxPrice,
                normalizeKeyword(keyword),
                PUBLIC_CATALOG_STATUS.name(),
                pageable
        );
    }

    public Optional<Product> findVisibleSellingProductById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return productRepository.findVisibleByProductIdAndLifecycleStatus(id, PUBLIC_CATALOG_STATUS);
    }

    @Transactional
    public void discontinueProduct(Product product) {
        product.setLifecycleStatus(ProductLifecycleStatus.DISCONTINUED);
        product.setStatus(false);
        productRepository.save(product);
    }

    private void prepareCatalogState(Product product) {
        if (product.getLifecycleStatus() == null) {
            product.setLifecycleStatus(ProductLifecycleStatus.SELLING);
        }
        if (product.getLifecycleStatus() != ProductLifecycleStatus.SELLING) {
            product.setStatus(false);
        }
    }

    private String normalizeKeyword(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? null : normalized;
    }
}
