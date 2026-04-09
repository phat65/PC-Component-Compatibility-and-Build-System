package com.example.PCOnlineShop.integration.component.provider;

import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;

import java.util.List;

public interface ComponentCatalogProvider {

    String getProviderCode();

    List<ImportedCatalogItemDto> fetchComponents(String keyword);
}
