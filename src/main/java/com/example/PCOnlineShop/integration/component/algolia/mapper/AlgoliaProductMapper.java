package com.example.PCOnlineShop.integration.component.algolia.mapper;

import com.example.PCOnlineShop.dto.build.ComponentDto;
import com.example.PCOnlineShop.integration.component.algolia.dto.AlgoliaProductDto;
import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import com.example.PCOnlineShop.integration.component.dto.ImportedProductDto;
import org.springframework.stereotype.Component;

@Component
public class AlgoliaProductMapper {

    private static final String PROVIDER_CODE = "algolia";

    public ImportedCatalogItemDto toImportedCatalogItem(AlgoliaProductDto source) {
        return ImportedCatalogItemDto.builder()
                .product(toImportedProduct(source))
                .component(toComponentDto(source))
                .build();
    }

    public ImportedProductDto toImportedProduct(AlgoliaProductDto source) {
        return ImportedProductDto.builder()
                .providerCode(PROVIDER_CODE)
                .externalId(source.getExternalId())
                .sku(source.getSku())
                .productName(source.getName())
                .brandName(source.getBrandName())
                .categoryName(normalizeCategory(source.getCategoryName()))
                .description(source.getDescription())
                .price(source.getPrice() != null ? source.getPrice().doubleValue() : null)
                .currency(source.getCurrency() != null ? source.getCurrency() : "USD")
                .imageUrl(source.getImageUrl())
                .productUrl(source.getProductUrl())
                .available(Boolean.TRUE.equals(source.getAvailable()))
                .build();
    }

    public ComponentDto toComponentDto(AlgoliaProductDto source) {
        return ComponentDto.builder()
                .productId(null)
                .productName(source.getName())
                .price(source.getPrice() != null ? source.getPrice().doubleValue() : null)
                .score(50)
                .category(normalizeCategory(source.getCategoryName()))
                .build();
    }

    private String normalizeCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return "Other";
        }

        String normalized = categoryName.trim().toLowerCase();
        if (normalized.contains("cpu") || normalized.contains("processor")) {
            return "CPU";
        }
        if (normalized.contains("gpu") || normalized.contains("graphics")) {
            return "GPU";
        }
        if (normalized.contains("motherboard") || normalized.contains("mainboard")) {
            return "Mainboard";
        }
        if (normalized.contains("memory") || normalized.contains("ram")) {
            return "Memory";
        }
        if (normalized.contains("storage") || normalized.contains("ssd") || normalized.contains("hdd")) {
            return "Storage";
        }
        if (normalized.contains("case") || normalized.contains("chassis")) {
            return "Case";
        }
        if (normalized.contains("power") || normalized.contains("psu")) {
            return "Power Supply";
        }
        if (normalized.contains("cool") || normalized.contains("fan") || normalized.contains("aio")) {
            return "Cooling";
        }
        return "Other";
    }
}
