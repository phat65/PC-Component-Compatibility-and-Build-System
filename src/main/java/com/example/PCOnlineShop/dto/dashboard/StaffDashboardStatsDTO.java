package com.example.PCOnlineShop.dto.dashboard;

import java.util.List;

public record StaffDashboardStatsDTO(
        long pendingOrders,
        long shippedOrders,
        long productsInStock,
        long feedbackCount,
        long todayOrders,
        long todayUnitsSold,
        long lowStockProducts,
        long deliveringOrders,
        List<DashboardPeriodStatsDTO> dailyOrderLoad,
        List<DashboardProductSalesDTO> topProducts,
        List<DashboardWorkloadDTO> workload
) {
}
