package com.example.PCOnlineShop.service.dashboard;

import com.example.PCOnlineShop.constant.OrderPaymentStatus;
import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.dto.dashboard.AdminDashboardStatsDTO;
import com.example.PCOnlineShop.dto.dashboard.DashboardPeriodStatsDTO;
import com.example.PCOnlineShop.dto.dashboard.DashboardProductSalesDTO;
import com.example.PCOnlineShop.dto.dashboard.DashboardWorkloadDTO;
import com.example.PCOnlineShop.dto.dashboard.StaffDashboardStatsDTO;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.repository.feedback.FeedbackRepository;
import com.example.PCOnlineShop.repository.order.OrderDetailRepository;
import com.example.PCOnlineShop.repository.order.OrderRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final int DAILY_WINDOW_DAYS = 7;
    private static final int MONTHLY_WINDOW_MONTHS = 6;
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int TOP_PRODUCT_LIMIT = 5;
    private static final ZoneId DASHBOARD_ZONE = ZoneId.systemDefault();

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminDashboardStatsDTO getAdminStats() {
        LocalDate today = LocalDate.now(DASHBOARD_ZONE);
        DateRange todayRange = rangeForDay(today);
        DateRange monthRange = rangeForMonth(YearMonth.from(today));
        DateRange yearRange = rangeForYear(today.getYear());

        return new AdminDashboardStatsDTO(
                accountRepository.count(),
                accountRepository.countByRole(RoleName.Staff),
                orderRepository.count(),
                productRepository.count(),
                safeRevenue(orderRepository.sumRecognizedRevenue(OrderPaymentStatus.PAID, OrderStatus.COMPLETED)),
                revenueBetween(todayRange),
                revenueBetween(monthRange),
                revenueBetween(yearRange),
                recognizedOrdersBetween(todayRange),
                recognizedOrdersBetween(monthRange),
                recognizedOrdersBetween(yearRange),
                unitsBetween(todayRange),
                unitsBetween(monthRange),
                unitsBetween(yearRange),
                buildDailyRevenueSeries(today),
                buildMonthlyRevenueSeries(today),
                topProductsBetween(yearRange),
                orderStatusBreakdown()
        );
    }

    public StaffDashboardStatsDTO getStaffStats() {
        LocalDate today = LocalDate.now(DASHBOARD_ZONE);
        DateRange todayRange = rangeForDay(today);
        DateRange yearRange = rangeForYear(today.getYear());

        return new StaffDashboardStatsDTO(
                orderRepository.countByStatusIn(List.of(OrderStatus.PENDING_PAYMENT, OrderStatus.PROCESSING)),
                orderRepository.countByStatus(OrderStatus.COMPLETED),
                productRepository.countByInventoryQuantityGreaterThan(0),
                feedbackRepository.count(),
                orderRepository.countByCreatedDateBetween(todayRange.start(), todayRange.end()),
                unitsBetween(todayRange),
                productRepository.countByInventoryQuantityLessThanEqual(LOW_STOCK_THRESHOLD),
                orderRepository.countByStatus(OrderStatus.DELIVERING),
                buildDailyOrderLoadSeries(today),
                topProductsBetween(yearRange),
                staffWorkload()
        );
    }

    private List<DashboardPeriodStatsDTO> buildDailyRevenueSeries(LocalDate today) {
        List<DashboardPeriodStatsDTO> points = new ArrayList<>();
        for (int offset = DAILY_WINDOW_DAYS - 1; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            DateRange range = rangeForDay(day);
            points.add(new DashboardPeriodStatsDTO(
                    day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    revenueBetween(range),
                    recognizedOrdersBetween(range),
                    unitsBetween(range),
                    0
            ));
        }
        return scaleByRevenue(points);
    }

    private List<DashboardPeriodStatsDTO> buildMonthlyRevenueSeries(LocalDate today) {
        YearMonth currentMonth = YearMonth.from(today);
        List<DashboardPeriodStatsDTO> points = new ArrayList<>();
        for (int offset = MONTHLY_WINDOW_MONTHS - 1; offset >= 0; offset--) {
            YearMonth month = currentMonth.minusMonths(offset);
            DateRange range = rangeForMonth(month);
            points.add(new DashboardPeriodStatsDTO(
                    month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    revenueBetween(range),
                    recognizedOrdersBetween(range),
                    unitsBetween(range),
                    0
            ));
        }
        return scaleByRevenue(points);
    }

    private List<DashboardPeriodStatsDTO> buildDailyOrderLoadSeries(LocalDate today) {
        List<DashboardPeriodStatsDTO> points = new ArrayList<>();
        for (int offset = DAILY_WINDOW_DAYS - 1; offset >= 0; offset--) {
            LocalDate day = today.minusDays(offset);
            DateRange range = rangeForDay(day);
            points.add(new DashboardPeriodStatsDTO(
                    day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    revenueBetween(range),
                    orderRepository.countByCreatedDateBetween(range.start(), range.end()),
                    unitsBetween(range),
                    0
            ));
        }
        return scaleByOrders(points);
    }

    private List<DashboardPeriodStatsDTO> scaleByRevenue(List<DashboardPeriodStatsDTO> points) {
        double max = points.stream()
                .mapToDouble(DashboardPeriodStatsDTO::revenue)
                .max()
                .orElse(0);
        return points.stream()
                .map(point -> withHeight(point, point.revenue(), max))
                .toList();
    }

    private List<DashboardPeriodStatsDTO> scaleByOrders(List<DashboardPeriodStatsDTO> points) {
        long max = points.stream()
                .mapToLong(DashboardPeriodStatsDTO::orderCount)
                .max()
                .orElse(0);
        return points.stream()
                .map(point -> withHeight(point, point.orderCount(), max))
                .toList();
    }

    private DashboardPeriodStatsDTO withHeight(DashboardPeriodStatsDTO point, double value, double max) {
        return new DashboardPeriodStatsDTO(
                point.label(),
                point.revenue(),
                point.orderCount(),
                point.unitsSold(),
                scaledHeight(value, max)
        );
    }

    private int scaledHeight(double value, double max) {
        if (max <= 0 || value <= 0) {
            return 6;
        }
        return Math.max(12, (int) Math.round((value / max) * 100));
    }

    private List<DashboardProductSalesDTO> topProductsBetween(DateRange range) {
        return orderDetailRepository.findTopRecognizedProductsBetween(
                        range.start(),
                        range.end(),
                        OrderPaymentStatus.PAID,
                        OrderStatus.COMPLETED,
                        PageRequest.of(0, TOP_PRODUCT_LIMIT)
                )
                .stream()
                .map(row -> new DashboardProductSalesDTO(
                        String.valueOf(row[0]),
                        toLong(row[1]),
                        toDouble(row[2])
                ))
                .toList();
    }

    private List<DashboardWorkloadDTO> orderStatusBreakdown() {
        return List.of(
                workload("Pending payment", orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT), "warning"),
                workload("Processing", orderRepository.countByStatus(OrderStatus.PROCESSING), "info"),
                workload("Ready to ship", orderRepository.countByStatus(OrderStatus.READY_TO_SHIP), "info"),
                workload("Ready for pickup", orderRepository.countByStatus(OrderStatus.READY_FOR_PICKUP), "info"),
                workload("Delivering", orderRepository.countByStatus(OrderStatus.DELIVERING), "info"),
                workload("Completed", orderRepository.countByStatus(OrderStatus.COMPLETED), "success"),
                workload("Cancelled", orderRepository.countByStatus(OrderStatus.CANCELLED), "danger")
        );
    }

    private List<DashboardWorkloadDTO> staffWorkload() {
        return List.of(
                workload("Pending payment", orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT), "warning"),
                workload("Processing", orderRepository.countByStatus(OrderStatus.PROCESSING), "warning"),
                workload("Ready to ship", orderRepository.countByStatus(OrderStatus.READY_TO_SHIP), "info"),
                workload("Ready for pickup", orderRepository.countByStatus(OrderStatus.READY_FOR_PICKUP), "info"),
                workload("Delivering", orderRepository.countByStatus(OrderStatus.DELIVERING), "info"),
                workload("Low stock", productRepository.countByInventoryQuantityLessThanEqual(LOW_STOCK_THRESHOLD), "danger")
        );
    }

    private DashboardWorkloadDTO workload(String label, long count, String tone) {
        return new DashboardWorkloadDTO(label, count, tone);
    }

    private double revenueBetween(DateRange range) {
        return safeRevenue(orderRepository.sumRecognizedRevenueBetween(
                range.start(),
                range.end(),
                OrderPaymentStatus.PAID,
                OrderStatus.COMPLETED
        ));
    }

    private long recognizedOrdersBetween(DateRange range) {
        return orderRepository.countRecognizedOrdersBetween(
                range.start(),
                range.end(),
                OrderPaymentStatus.PAID,
                OrderStatus.COMPLETED
        );
    }

    private long unitsBetween(DateRange range) {
        Long units = orderDetailRepository.sumRecognizedUnitsBetween(
                range.start(),
                range.end(),
                OrderPaymentStatus.PAID,
                OrderStatus.COMPLETED
        );
        return units != null ? units : 0;
    }

    private double safeRevenue(Double amount) {
        return amount != null ? amount : 0;
    }

    private long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0;
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0;
    }

    private DateRange rangeForDay(LocalDate day) {
        Date date = toDate(day);
        return new DateRange(date, date);
    }

    private DateRange rangeForMonth(YearMonth month) {
        return new DateRange(toDate(month.atDay(1)), toDate(month.atEndOfMonth()));
    }

    private DateRange rangeForYear(int year) {
        return new DateRange(toDate(LocalDate.of(year, 1, 1)), toDate(LocalDate.of(year, 12, 31)));
    }

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(DASHBOARD_ZONE).toInstant());
    }

    private record DateRange(Date start, Date end) {
    }
}
