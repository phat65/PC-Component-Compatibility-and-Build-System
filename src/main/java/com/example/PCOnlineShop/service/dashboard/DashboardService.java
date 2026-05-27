package com.example.PCOnlineShop.service.dashboard;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.dto.dashboard.AdminDashboardStatsDTO;
import com.example.PCOnlineShop.dto.dashboard.StaffDashboardStatsDTO;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.repository.feedback.FeedbackRepository;
import com.example.PCOnlineShop.repository.order.OrderRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final String ORDER_READY_TO_SHIP = "Ready to Ship";
    private static final String ORDER_COMPLETED = "Completed";

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminDashboardStatsDTO getAdminStats() {
        return new AdminDashboardStatsDTO(
                accountRepository.count(),
                accountRepository.countByRole(RoleName.Staff),
                orderRepository.count(),
                productRepository.count(),
                calculateRevenue()
        );
    }

    public StaffDashboardStatsDTO getStaffStats() {
        return new StaffDashboardStatsDTO(
                orderRepository.countByStatus(ORDER_READY_TO_SHIP),
                orderRepository.countByStatus(ORDER_COMPLETED),
                productRepository.count(),
                feedbackRepository.count()
        );
    }

    private double calculateRevenue() {
        return orderRepository.findAll()
                .stream()
                .map(Order::getFinalAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
