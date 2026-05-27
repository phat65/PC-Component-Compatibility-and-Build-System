package com.example.PCOnlineShop.dto.dashboard;

public record AdminDashboardStatsDTO(
        long totalUsers,
        long totalStaff,
        long totalOrders,
        long totalProducts,
        double revenue
) {
}
