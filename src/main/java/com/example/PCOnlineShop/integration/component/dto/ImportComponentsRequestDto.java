package com.example.PCOnlineShop.integration.component.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportComponentsRequestDto {

    private String keyword;
    private List<String> externalIds;
}
