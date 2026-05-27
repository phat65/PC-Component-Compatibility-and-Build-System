package com.example.PCOnlineShop.dto.dashboard;

public record StaffDashboardStatsDTO(
        long pendingOrders,
        long shippedOrders,
        long productsInStock,
        long feedbackCount
) {
}
