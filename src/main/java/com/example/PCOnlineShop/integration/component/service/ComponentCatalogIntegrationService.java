package com.example.PCOnlineShop.integration.component.service;

import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import com.example.PCOnlineShop.integration.component.provider.ComponentCatalogProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ComponentCatalogIntegrationService {

    private final Map<String, ComponentCatalogProvider> providersByCode;

    public ComponentCatalogIntegrationService(List<ComponentCatalogProvider> providers) {
        this.providersByCode = providers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        provider -> normalize(provider.getProviderCode()),
                        Function.identity()
                ));
    }

    public List<ImportedCatalogItemDto> fetchComponents(String providerCode, String keyword) {
        return resolveProvider(providerCode).fetchComponents(keyword);
    }

    public ComponentCatalogProvider resolveProvider(String providerCode) {
        ComponentCatalogProvider provider = providersByCode.get(normalize(providerCode));
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported component provider: " + providerCode);
        }
        return provider;
    }

    private String normalize(String providerCode) {
        return providerCode == null ? "" : providerCode.trim().toLowerCase(Locale.ROOT);
    }
}
