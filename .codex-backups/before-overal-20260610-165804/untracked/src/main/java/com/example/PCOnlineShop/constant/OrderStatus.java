package com.example.PCOnlineShop.constant;

import java.util.List;

public final class OrderStatus {
    public static final String PENDING_PAYMENT = "Pending Payment";
    public static final String PROCESSING = "Processing";
    public static final String READY_TO_SHIP = "Ready to Ship";
    public static final String DELIVERING = "Delivering";
    public static final String COMPLETED = "Completed";
    public static final String CANCELLED = "Cancelled";
    public static final String DELIVERY_FAILED = "Delivery Failed";

    public static final List<String> SHIPPING_QUEUE = List.of(READY_TO_SHIP, DELIVERING);
    public static final List<String> ACTIVE_PRODUCT_REFERENCE_STATUSES = List.of(
            PENDING_PAYMENT,
            PROCESSING,
            READY_TO_SHIP,
            DELIVERING
    );

    private OrderStatus() {
    }
}
