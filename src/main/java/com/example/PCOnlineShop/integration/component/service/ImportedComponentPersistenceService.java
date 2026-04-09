package com.example.PCOnlineShop.integration.component.service;

import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import com.example.PCOnlineShop.integration.component.dto.ImportedProductDto;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.ExternalProductSource;
import com.example.PCOnlineShop.model.product.Image;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;
import com.example.PCOnlineShop.repository.product.BrandRepository;
import com.example.PCOnlineShop.repository.product.CategoryRepository;
import com.example.PCOnlineShop.repository.product.ExternalProductSourceRepository;
import com.example.PCOnlineShop.repository.product.ImageRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ImportedComponentPersistenceService {

    private static final int DEFAULT_CATEGORY_ORDER = 999;
    private static final int DEFAULT_INVENTORY_QUANTITY = 1;
    private static final int DEFAULT_PERFORMANCE_SCORE = 50;

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ExternalProductSourceRepository externalProductSourceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PersistImportedComponentResult persistImportedComponent(ImportedCatalogItemDto catalogItem) {
        ImportedProductDto importedProduct = catalogItem != null ? catalogItem.getProduct() : null;
        if (!isValid(importedProduct)) {
            return PersistImportedComponentResult.skip();
        }

        ExternalProductSource source = externalProductSourceRepository
                .findByProviderCodeIgnoreCaseAndExternalId(
                        importedProduct.getProviderCode(),
                        importedProduct.getExternalId()
                )
                .orElse(null);

        boolean created = source == null;
        Product product = created ? new Product() : source.getProduct();

        product.setProductName(importedProduct.getProductName().trim());
        product.setPrice(importedProduct.getPrice());
        product.setDescription(importedProduct.getDescription());
        product.setSpecification(buildSpecification(importedProduct, product.getSpecification()));
        product.setCreateAt(product.getCreateAt() != null ? product.getCreateAt() : new Date());
        product.setStatus(created ? importedProduct.isAvailable() : product.isStatus());
        product.setLifecycleStatus(importedProduct.isAvailable()
                ? ProductLifecycleStatus.SELLING
                : ProductLifecycleStatus.DRAFT);
        if (product.getLifecycleStatus() != ProductLifecycleStatus.SELLING) {
            product.setStatus(false);
        }
        product.setInventoryQuantity(resolveInventoryQuantity(product, importedProduct.isAvailable(), created));
        product.setPerformanceScore(resolvePerformanceScore(product, catalogItem, created));
        product.setBrand(resolveBrand(importedProduct));
        product.setCategories(mergeCategories(product, resolveCategory(importedProduct)));

        Product savedProduct = productRepository.save(product);
        upsertImage(savedProduct, importedProduct.getImageUrl());
        upsertExternalSource(source, savedProduct, importedProduct);

        return PersistImportedComponentResult.saved(savedProduct.getProductId(), created);
    }

    private boolean isValid(ImportedProductDto importedProduct) {
        return importedProduct != null
                && hasText(importedProduct.getProviderCode())
                && hasText(importedProduct.getExternalId())
                && hasText(importedProduct.getProductName())
                && importedProduct.getPrice() != null
                && importedProduct.getPrice() > 0;
    }

    private Brand resolveBrand(ImportedProductDto importedProduct) {
        String brandName = hasText(importedProduct.getBrandName())
                ? importedProduct.getBrandName().trim()
                : capitalize(importedProduct.getProviderCode());

        return brandRepository.findByNameIgnoreCase(brandName)
                .orElseGet(() -> brandRepository.save(createBrand(brandName)));
    }

    private Category resolveCategory(ImportedProductDto importedProduct) {
        String categoryName = hasText(importedProduct.getCategoryName())
                ? importedProduct.getCategoryName().trim()
                : "Other";

        return categoryRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepository.save(createCategory(categoryName)));
    }

    private Brand createBrand(String brandName) {
        Brand brand = new Brand();
        brand.setName(brandName);
        brand.setDescription("Imported external brand");
        brand.setStatus(Boolean.TRUE);
        return brand;
    }

    private Category createCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setDescription("Imported external category");
        category.setDisplayOrder(DEFAULT_CATEGORY_ORDER);
        category.setCreatedAt(new Date());
        return category;
    }

    private String buildSpecification(ImportedProductDto importedProduct, String currentSpecification) {
        if (hasText(currentSpecification)) {
            return currentSpecification;
        }
        if (hasText(importedProduct.getSku())) {
            return "SKU: " + importedProduct.getSku().trim();
        }
        return null;
    }

    private int resolvePerformanceScore(Product product, ImportedCatalogItemDto catalogItem, boolean created) {
        if (!created && product.getPerformanceScore() != null && product.getPerformanceScore() > 0) {
            return product.getPerformanceScore();
        }

        Integer score = catalogItem != null && catalogItem.getComponent() != null
                ? catalogItem.getComponent().getScore()
                : null;
        return score != null && score > 0 ? score : DEFAULT_PERFORMANCE_SCORE;
    }

    private int resolveInventoryQuantity(Product product, boolean available, boolean created) {
        if (!created && product.getInventoryQuantity() != null) {
            return product.getInventoryQuantity();
        }
        return available ? DEFAULT_INVENTORY_QUANTITY : 0;
    }

    private List<Category> mergeCategories(Product product, Category importedCategory) {
        List<Category> categories = product.getCategories() != null
                ? new ArrayList<>(product.getCategories())
                : new ArrayList<>();

        boolean hasCategory = categories.stream()
                .anyMatch(existing -> existing.getCategoryId() == importedCategory.getCategoryId());

        if (!hasCategory) {
            categories.add(importedCategory);
        }

        return categories;
    }

    private void upsertImage(Product product, String imageUrl) {
        if (!hasText(imageUrl)) {
            return;
        }

        List<Image> images = product.getImages();
        if (images == null || images.isEmpty()) {
            Image image = new Image();
            image.setProduct(product);
            image.setImageUrl(imageUrl.trim());
            image.setCreatedAt(new Date());
            imageRepository.save(image);
            product.setImages(new ArrayList<>(List.of(image)));
            return;
        }

        Image primaryImage = images.getFirst();
        primaryImage.setImageUrl(imageUrl.trim());
        if (primaryImage.getCreatedAt() == null) {
            primaryImage.setCreatedAt(new Date());
        }
        imageRepository.save(primaryImage);
    }

    private void upsertExternalSource(ExternalProductSource source, Product product, ImportedProductDto importedProduct) {
        ExternalProductSource externalSource = source != null ? source : new ExternalProductSource();
        externalSource.setProduct(product);
        externalSource.setProviderCode(importedProduct.getProviderCode().trim().toLowerCase(Locale.ROOT));
        externalSource.setExternalId(importedProduct.getExternalId().trim());
        externalSource.setSku(trimToNull(importedProduct.getSku()));
        externalSource.setCurrency(trimToNull(importedProduct.getCurrency()));
        externalSource.setProductUrl(trimToNull(importedProduct.getProductUrl()));
        externalProductSourceRepository.save(externalSource);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private String capitalize(String value) {
        if (!hasText(value)) {
            return "External";
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    public record PersistImportedComponentResult(Integer productId, boolean created, boolean skipped) {

        public static PersistImportedComponentResult saved(Integer productId, boolean created) {
            return new PersistImportedComponentResult(productId, created, false);
        }

        public static PersistImportedComponentResult skip() {
            return new PersistImportedComponentResult(null, false, true);
        }
    }
}
