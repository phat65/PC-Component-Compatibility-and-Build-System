package com.example.PCOnlineShop.dto.build;

import lombok.Data;

@Data
public class BuildRequestDto {
    private String preset;  // e.g., "GAMING_HIGH"
    private double budget;  // VND, e.g., 50000000
}

