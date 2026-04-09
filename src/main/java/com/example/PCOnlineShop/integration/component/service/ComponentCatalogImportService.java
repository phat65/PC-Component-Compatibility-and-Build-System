package com.example.PCOnlineShop.integration.component.service;

import com.example.PCOnlineShop.integration.component.dto.ImportComponentsRequestDto;
import com.example.PCOnlineShop.integration.component.dto.ImportComponentsResultDto;
import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComponentCatalogImportService {

    private final ComponentCatalogIntegrationService componentCatalogIntegrationService;
    private final ImportedComponentPersistenceService importedComponentPersistenceService;

    public ImportComponentsResultDto importComponents(String providerCode, ImportComponentsRequestDto request) {
        String keyword = request != null ? request.getKeyword() : null;
        List<ImportedCatalogItemDto> fetchedItems = componentCatalogIntegrationService.fetchComponents(providerCode, keyword);
        List<ImportedCatalogItemDto> selectedItems = filterSelectedItems(fetchedItems, request != null ? request.getExternalIds() : null);

        int createdCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        List<Integer> productIds = new ArrayList<>();

        for (ImportedCatalogItemDto selectedItem : selectedItems) {
            ImportedComponentPersistenceService.PersistImportedComponentResult result;
            try {
                result = importedComponentPersistenceService.persistImportedComponent(selectedItem);
            } catch (RuntimeException ex) {
                log.warn("Failed to persist imported component from provider {}", providerCode, ex);
                skippedCount++;
                continue;
            }

            if (result.skipped()) {
                skippedCount++;
                continue;
            }

            if (result.created()) {
                createdCount++;
            } else {
                updatedCount++;
            }

            productIds.add(result.productId());
        }

        return ImportComponentsResultDto.builder()
                .fetchedCount(fetchedItems.size())
                .selectedCount(selectedItems.size())
                .createdCount(createdCount)
                .updatedCount(updatedCount)
                .skippedCount(skippedCount)
                .productIds(productIds)
                .build();
    }

    private List<ImportedCatalogItemDto> filterSelectedItems(List<ImportedCatalogItemDto> fetchedItems, List<String> externalIds) {
        if (externalIds == null || externalIds.isEmpty()) {
            return fetchedItems;
        }

        Set<String> requestedIds = externalIds.stream()
                .map(this::normalize)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toSet());

        if (requestedIds.isEmpty()) {
            return fetchedItems;
        }

        return fetchedItems.stream()
                .filter(item -> item != null && item.getProduct() != null)
                .filter(item -> requestedIds.contains(normalize(item.getProduct().getExternalId())))
                .toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
