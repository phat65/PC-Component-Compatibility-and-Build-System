package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.product.BrandRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private static final double BRAND_MERGE_SIMILARITY_THRESHOLD = 0.6;

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Optional<Brand> findBrandById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return brandRepository.findById(id);
    }

    public Brand getRequiredBrand(Integer id) {
        return findBrandById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    @Transactional
    public Brand createBrand(Brand brand) {
        normalizeBrand(brand);
        validateUniqueName(brand.getName(), null);

        if (brand.getStatus() == null) {
            brand.setStatus(true);
        }

        return brandRepository.save(brand);
    }

    @Transactional
    public Brand updateBrand(Integer id, Brand incoming) {
        Brand existing = getRequiredBrand(id);

        normalizeBrand(incoming);
        validateUniqueName(incoming.getName(), id);

        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setWebsite(incoming.getWebsite());
        existing.setStatus(Boolean.TRUE.equals(incoming.getStatus()));

        return brandRepository.save(existing);
    }

    @Transactional
    public Brand updateBrandStatus(Integer id, boolean status) {
        Brand brand = getRequiredBrand(id);

        if (!status && countProductsOfBrand(id) > 0) {
            throw new IllegalArgumentException("Cannot deactivate brand that still has products");
        }

        brand.setStatus(status);
        return brandRepository.save(brand);
    }

    @Transactional
    public long mergeBrands(Integer sourceId, Integer targetId) {
        if (sourceId == null || targetId == null) {
            throw new IllegalArgumentException("Brand id is required");
        }
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("Cannot merge a brand into itself");
        }

        Brand source = getRequiredBrand(sourceId);
        Brand target = getRequiredBrand(targetId);
        if (!areSimilarBrand(source.getName(), target.getName())) {
            throw new IllegalArgumentException("Cannot merge brands that are not similar");
        }

        long productCount = countProductsOfBrand(sourceId);
        productRepository.reassignBrandByIds(sourceId, targetId);
        source.setStatus(false);
        brandRepository.save(source);
        return productCount;
    }

    private boolean areSimilarBrand(String name1, String name2) {
        if (name1 == null || name2 == null) {
            return false;
        }

        String normalizedName1 = name1.toLowerCase().trim();
        String normalizedName2 = name2.toLowerCase().trim();
        if (normalizedName1.isEmpty() || normalizedName2.isEmpty()) {
            return false;
        }

        int distance = levenshteinDistance(normalizedName1, normalizedName2);
        int maxLength = Math.max(normalizedName1.length(), normalizedName2.length());
        double similarity = 1 - (double) distance / maxLength;
        return similarity >= BRAND_MERGE_SIMILARITY_THRESHOLD;
    }

    private long countProductsOfBrand(Integer brandId) {
        if (brandId == null) {
            return 0;
        }
        return productRepository.countByBrand_BrandId(brandId);
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }

    private void validateUniqueName(String name, Integer currentBrandId) {
        if (!StringUtils.hasText(name)) {
            return;
        }

        brandRepository.findByNameIgnoreCase(name.trim())
                .filter(existing -> currentBrandId == null || !existing.getBrandId().equals(currentBrandId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Brand name already exists");
                });
    }

    private void normalizeBrand(Brand brand) {
        if (brand == null) {
            throw new IllegalArgumentException("Brand is required");
        }

        brand.setName(normalize(brand.getName()));
        brand.setDescription(normalize(brand.getDescription()));
        brand.setWebsite(normalize(brand.getWebsite()));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
