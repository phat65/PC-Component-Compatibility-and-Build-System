package com.example.PCOnlineShop.dto.dashboard;

public record DashboardPeriodStatsDTO(
        String label,
        double revenue,
        long orderCount,
        long unitsSold,
        int heightPercent
) {
}
