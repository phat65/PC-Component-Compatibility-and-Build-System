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

        return ADMIN_DASHBOARD_VIEW;
    }

    @GetMapping("/dashboard/staff")
    public String staffDashboard(Model model) {
        StaffDashboardStatsDTO stats = dashboardService.getStaffStats();
        model.addAttribute("pendingOrders", stats.pendingOrders());
        model.addAttribute("shippedOrders", stats.shippedOrders());
        model.addAttribute("productsInStock", stats.productsInStock());
        model.addAttribute("feedbackCount", stats.feedbackCount());

        return STAFF_DASHBOARD_VIEW;
    }
}
