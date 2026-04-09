package com.example.PCOnlineShop.integration.component.dto;

import com.example.PCOnlineShop.dto.build.ComponentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedCatalogItemDto {

    private ImportedProductDto product;
    private ComponentDto component;
}
