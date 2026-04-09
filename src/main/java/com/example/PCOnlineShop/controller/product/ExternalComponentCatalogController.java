package com.example.PCOnlineShop.controller.product;

import com.example.PCOnlineShop.integration.component.dto.ImportComponentsRequestDto;
import com.example.PCOnlineShop.integration.component.dto.ImportComponentsResultDto;
import com.example.PCOnlineShop.integration.component.dto.ImportedCatalogItemDto;
import com.example.PCOnlineShop.integration.component.service.ComponentCatalogImportService;
import com.example.PCOnlineShop.integration.component.service.ComponentCatalogIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.catalog.external-import.enabled", havingValue = "true")
@RequestMapping("/staff/products/external-components")
public class ExternalComponentCatalogController {

    private final ComponentCatalogIntegrationService componentCatalogIntegrationService;
    private final ComponentCatalogImportService componentCatalogImportService;

    @GetMapping("/providers/{providerCode}")
    public ResponseEntity<List<ImportedCatalogItemDto>> fetchComponents(
            @PathVariable String providerCode,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(componentCatalogIntegrationService.fetchComponents(providerCode, keyword));
    }

    @PostMapping("/providers/{providerCode}/import")
    public ResponseEntity<ImportComponentsResultDto> importComponents(
            @PathVariable String providerCode,
            @RequestBody(required = false) ImportComponentsRequestDto request
    ) {
        return ResponseEntity.ok(componentCatalogImportService.importComponents(providerCode, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
