package com.example.PCOnlineShop.integration.component.algolia.provider;

import com.example.PCOnlineShop.integration.component.algolia.client.AlgoliaApiClient;
import com.example.PCOnlineShop.integration.component.algolia.mapper.AlgoliaProductMapper;
import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import com.example.PCOnlineShop.integration.component.provider.ComponentCatalogProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlgoliaComponentProvider implements ComponentCatalogProvider {

    private static final String PROVIDER_CODE = "algolia";

    private final AlgoliaApiClient algoliaApiClient;
    private final AlgoliaProductMapper algoliaProductMapper;

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public List<ImportedCatalogItemDto> fetchComponents(String keyword) {
        return algoliaApiClient.searchComponents(keyword).getProducts().stream()
                .map(algoliaProductMapper::toImportedCatalogItem)
                .toList();
    }
}
