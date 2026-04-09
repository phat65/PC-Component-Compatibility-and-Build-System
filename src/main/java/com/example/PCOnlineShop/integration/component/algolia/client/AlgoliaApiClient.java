package com.example.PCOnlineShop.integration.component.algolia.client;

import com.example.PCOnlineShop.integration.component.algolia.dto.AlgoliaSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class AlgoliaApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${integration.component.algolia.base-url:}")
    private String baseUrl;

    @Value("${integration.component.algolia.index-name:}")
    private String indexName;

    @Value("${integration.component.algolia.search-path:/1/indexes/{indexName}/query}")
    private String searchPath;

    @Value("${integration.component.algolia.application-id:}")
    private String applicationId;

    @Value("${integration.component.algolia.api-key:}")
    private String apiKey;

    @Value("${integration.component.algolia.hits-per-page:20}")
    private int hitsPerPage;

    public AlgoliaSearchResponse searchComponents(String keyword) {
        if (applicationId == null || applicationId.isBlank()
                || apiKey == null || apiKey.isBlank()
                || indexName == null || indexName.isBlank()) {
            log.warn("Algolia integration is missing required configuration");
            return AlgoliaSearchResponse.empty();
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(resolveBaseUrl())
                .path(searchPath)
                .buildAndExpand(Map.of("indexName", indexName))
                .toUri();

        try {
            ResponseEntity<AlgoliaSearchResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    new HttpEntity<>(buildRequestBody(keyword), buildHeaders()),
                    AlgoliaSearchResponse.class
            );
            return response.getBody() != null ? response.getBody() : AlgoliaSearchResponse.empty();
        } catch (RestClientException ex) {
            log.warn("Failed to fetch Algolia components from {}", uri, ex);
            return AlgoliaSearchResponse.empty();
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
        headers.set("x-algolia-application-id", applicationId);
        headers.set("x-algolia-api-key", apiKey);
        return headers;
    }

    private Map<String, Object> buildRequestBody(String keyword) {
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("query", keyword == null ? "" : keyword.trim());
        requestBody.put("hitsPerPage", hitsPerPage);
        return requestBody;
    }

    private String resolveBaseUrl() {
        if (baseUrl != null && !baseUrl.isBlank()) {
            return baseUrl;
        }
        return "https://" + applicationId + "-dsn.algolia.net";
    }
}
