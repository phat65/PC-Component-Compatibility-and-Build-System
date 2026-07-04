package com.example.PCOnlineShop.controller.dashboard;

import com.example.PCOnlineShop.dto.dashboard.AdminDashboardStatsDTO;
import com.example.PCOnlineShop.dto.dashboard.StaffDashboardStatsDTO;
import com.example.PCOnlineShop.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private static final String ADMIN_DASHBOARD_VIEW = "dashboard-admin";
    private static final String STAFF_DASHBOARD_VIEW = "dashboard-staff";

    private final DashboardService dashboardService;

    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model) {
        AdminDashboardStatsDTO stats = dashboardService.getAdminStats();
        model.addAttribute("totalUsers", stats.totalUsers());
        model.addAttribute("totalStaff", stats.totalStaff());
        model.addAttribute("totalOrders", stats.totalOrders());
        model.addAttribute("totalProducts", stats.totalProducts());
        model.addAttribute("revenue", stats.revenue());
        model.addAttribute("todayRevenue", stats.todayRevenue());
        model.addAttribute("monthRevenue", stats.monthRevenue());
        model.addAttribute("yearRevenue", stats.yearRevenue());
        model.addAttribute("todayOrders", stats.todayOrders());
        model.addAttribute("monthOrders", stats.monthOrders());
        model.addAttribute("yearOrders", stats.yearOrders());
        model.addAttribute("todayUnitsSold", stats.todayUnitsSold());
        model.addAttribute("monthUnitsSold", stats.monthUnitsSold());
        model.addAttribute("yearUnitsSold", stats.yearUnitsSold());
        model.addAttribute("dailyRevenue", stats.dailyRevenue());
        model.addAttribute("monthlyRevenue", stats.monthlyRevenue());
        model.addAttribute("topProducts", stats.topProducts());
        model.addAttribute("orderStatusBreakdown", stats.orderStatusBreakdown());

        return ADMIN_DASHBOARD_VIEW;
    }

    @GetMapping("/dashboard/staff")
    public String staffDashboard(Model model) {
        StaffDashboardStatsDTO stats = dashboardService.getStaffStats();
        model.addAttribute("pendingOrders", stats.pendingOrders());
        model.addAttribute("shippedOrders", stats.shippedOrders());
        model.addAttribute("productsInStock", stats.productsInStock());
        model.addAttribute("feedbackCount", stats.feedbackCount());
        model.addAttribute("todayOrders", stats.todayOrders());
        model.addAttribute("todayUnitsSold", stats.todayUnitsSold());
        model.addAttribute("lowStockProducts", stats.lowStockProducts());
        model.addAttribute("deliveringOrders", stats.deliveringOrders());
        model.addAttribute("dailyOrderLoad", stats.dailyOrderLoad());
        model.addAttribute("topProducts", stats.topProducts());
        model.addAttribute("workload", stats.workload());

        return STAFF_DASHBOARD_VIEW;
    }
}
