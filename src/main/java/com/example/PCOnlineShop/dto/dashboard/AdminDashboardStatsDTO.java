package com.example.PCOnlineShop.dto.dashboard;

import java.util.List;

public record AdminDashboardStatsDTO(
        long totalUsers,
        long totalStaff,
        long totalOrders,
        long totalProducts,
        double revenue,
        double todayRevenue,
        double monthRevenue,
        double yearRevenue,
        long todayOrders,
        long monthOrders,
        long yearOrders,
        long todayUnitsSold,
        long monthUnitsSold,
        long yearUnitsSold,
        List<DashboardPeriodStatsDTO> dailyRevenue,
        List<DashboardPeriodStatsDTO> monthlyRevenue,
        List<DashboardProductSalesDTO> topProducts,
        List<DashboardWorkloadDTO> orderStatusBreakdown
) {
}
