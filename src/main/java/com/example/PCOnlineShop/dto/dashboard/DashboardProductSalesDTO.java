package com.example.PCOnlineShop.dto.dashboard;

public record DashboardProductSalesDTO(
        String productName,
        long unitsSold,
        double revenue
) {
}
