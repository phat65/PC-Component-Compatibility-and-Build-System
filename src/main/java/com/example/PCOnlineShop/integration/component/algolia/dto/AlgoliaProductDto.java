package com.example.PCOnlineShop.integration.component.algolia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlgoliaProductDto {

    @JsonAlias({"objectID", "id", "uid", "productId"})
    private String externalId;

    @JsonAlias({"sku", "partNumber"})
    private String sku;

    @JsonAlias({"name", "productName", "title"})
    private String name;

    @JsonAlias({"brand", "brandName", "manufacturer"})
    private String brandName;

    @JsonAlias({"category", "categoryName", "productType", "type"})
    private String categoryName;

    @JsonAlias({"description", "shortDescription"})
    private String description;

    @JsonAlias({"url", "productUrl", "link", "slug"})
    private String productUrl;

    @JsonAlias({"image", "imageUrl", "thumbnail", "image_link"})
    private String imageUrl;

    @JsonAlias({"price", "salePrice", "listPrice", "finalPrice"})
    private BigDecimal price;

    @JsonAlias({"currency", "currencyCode"})
    private String currency;

    @JsonAlias({"inStock", "available", "isAvailable"})
    private Boolean available;
}
