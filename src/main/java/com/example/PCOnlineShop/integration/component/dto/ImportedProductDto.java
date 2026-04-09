package com.example.PCOnlineShop.integration.component.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedProductDto {

    private String providerCode;
    private String externalId;
    private String sku;
    private String productName;
    private String brandName;
    private String categoryName;
    private String description;
    private Double price;
    private String currency;
    private String imageUrl;
    private String productUrl;
    private boolean available;
}
