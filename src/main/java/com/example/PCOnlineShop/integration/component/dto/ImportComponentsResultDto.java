package com.example.PCOnlineShop.integration.component.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportComponentsResultDto {

    private int fetchedCount;
    private int selectedCount;
    private int createdCount;
    private int updatedCount;
    private int skippedCount;
    private List<Integer> productIds;
}
