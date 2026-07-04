package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Image;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.model.product.ProductLifecycleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductService productService;
    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ComponentSpecService componentSpecService;

    @Transactional(rollbackFor = Exception.class)
    public void createProduct(Product product,
                              List<Integer> categoryIds,
                              Integer brandId,
                              Map<String, String> params,
                              List<MultipartFile> imageFiles) throws IOException {
        productImageService.validateNewProductImages(imageFiles);

        List<Category> categories = categoryIds.stream()
                .map(categoryService::getRequiredCategory)
                .toList();
        Brand brand = brandService.getRequiredBrand(brandId);

        product.setCategories(new ArrayList<>(categories));
        product.setBrand(brand);
        applyCatalogVisibility(product);

        Product saved = productService.saveProduct(product);
        List<Image> storedImages = List.of();
        try {
            storedImages = productImageService.storeProductImages(saved, imageFiles);
            saved.setImages(storedImages);
            componentSpecService.saveProductSpec(saved, categoryIds.get(0), params);
        } catch (IOException | RuntimeException ex) {
            productImageService.deleteStoredImageFiles(storedImages);
            throw ex;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product incoming,
                              List<Integer> categoryIds,
                              Map<String, String> params,
                              List<MultipartFile> imageFiles,
                              String deleteImageIds) throws IOException {
        Product current = productService.getRequiredProduct(incoming.getProductId());
        Set<Integer> deletedImageIds = parseDeleteImageIds(deleteImageIds);
        int remainingImageCount = countRemainingImages(current, deletedImageIds);
        productImageService.validateProductImageUpload(imageFiles, remainingImageCount);

        Integer currentPrimaryCategoryId = getPrimaryCategoryId(current);
        Integer incomingPrimaryCategoryId = categoryIds != null && !categoryIds.isEmpty()
                ? categoryIds.get(0)
                : currentPrimaryCategoryId;

        if (incomingPrimaryCategoryId != null && !incomingPrimaryCategoryId.equals(currentPrimaryCategoryId)) {
            throw new IllegalArgumentException("Cannot change primary category of an existing product. Specs will not be updated.");
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> newCategories = categoryIds.stream()
                    .map(categoryService::getRequiredCategory)
                    .toList();
            current.setCategories(new ArrayList<>(newCategories));
        }

        current.setProductName(incoming.getProductName());
        current.setDescription(incoming.getDescription());
        current.setPrice(incoming.getPrice());
        current.setStatus(incoming.isStatus());
        current.setLifecycleStatus(incoming.getLifecycleStatus());
        current.setInventoryQuantity(incoming.getInventoryQuantity());
        if (incoming.getBrand() != null && incoming.getBrand().getBrandId() > 0) {
            current.setBrand(brandService.getRequiredBrand(incoming.getBrand().getBrandId()));
        }
        applyCatalogVisibility(current);

        Product updated = productService.saveProduct(current);

        if (incomingPrimaryCategoryId != null && hasSpecParams(params)) {
            componentSpecService.saveProductSpec(updated, incomingPrimaryCategoryId, params);
        }

        List<Image> storedImages = List.of();
        try {
            storedImages = productImageService.storeProductImages(updated, imageFiles);
            deleteProductImages(deletedImageIds);
        } catch (IOException | RuntimeException ex) {
            productImageService.deleteStoredImageFiles(storedImages);
            throw ex;
        }
    }

    @Transactional
    public void discontinueProductIfAllowed(Integer productId) {
        Product product = productService.getRequiredProduct(productId);
        if (productService.hasActiveOrderReferences(productId)) {
            throw new IllegalStateException("This product is currently in active orders and cannot be hidden.");
        }
        productService.discontinueProduct(product);
    }

    private Set<Integer> parseDeleteImageIds(String deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isBlank()) {
            return Set.of();
        }

        Set<Integer> imageIds = new LinkedHashSet<>();
        Arrays.stream(deleteImageIds.split(","))
                .filter(value -> !value.isBlank())
                .map(String::trim)
                .map(Integer::parseInt)
                .forEach(imageIds::add);
        return imageIds;
    }

    private int countRemainingImages(Product product, Set<Integer> deletedImageIds) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return 0;
        }

        return (int) product.getImages().stream()
                .filter(image -> image != null && !deletedImageIds.contains(image.getImageId()))
                .count();
    }

    private void deleteProductImages(Set<Integer> deleteImageIds) {
        if (deleteImageIds == null || deleteImageIds.isEmpty()) {
            return;
        }

        deleteImageIds.forEach(productImageService::deleteProductImageById);
    }

    private void applyCatalogVisibility(Product product) {
        if (product.getLifecycleStatus() == null) {
            product.setLifecycleStatus(ProductLifecycleStatus.SELLING);
        }
        if (product.getLifecycleStatus() != ProductLifecycleStatus.SELLING) {
            product.setStatus(false);
        }
    }

    private Integer getPrimaryCategoryId(Product product) {
        if (product.getCategories() == null || product.getCategories().isEmpty()) {
            return null;
        }
        return product.getCategories().getFirst().getCategoryId();
    }

    private boolean hasSpecParams(Map<String, String> params) {
        return params != null && params.keySet().stream()
                .anyMatch(key -> key.matches("^(mainboard|cpu|gpu|memory|storage|pcase|psu|cl)\\..*"));
    }
}
