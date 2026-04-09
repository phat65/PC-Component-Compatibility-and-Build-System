package com.example.PCOnlineShop.integration.component.algolia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlgoliaSearchResponse {

    @JsonAlias({"items", "results", "products", "hits"})
    private List<AlgoliaProductDto> products = new ArrayList<>();

    public static AlgoliaSearchResponse empty() {
        return new AlgoliaSearchResponse();
    }
}
