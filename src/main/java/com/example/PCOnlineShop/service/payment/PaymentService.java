package com.example.PCOnlineShop.service.payment;

import com.example.PCOnlineShop.dto.payment.PaymentInfoDTO;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.order.OrderDetail;
import com.example.PCOnlineShop.model.payment.Payment;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.service.order.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class PaymentService {

    private static final String PAYMENT_PENDING = "PENDING";
    private static final String PAYMENT_SUCCESS = "SUCCESS";
    private static final String PAYOS_STATUS_PAID = "PAID";
    private static final String PAYOS_SUCCESS_CODE = "00";
    private static final String ORDER_PENDING_PAYMENT = "Pending Payment";
    private static final String ORDER_READY_TO_SHIP = "Ready to Ship";
    private static final String ORDER_PAYMENT_PAID = "PAID";

    private final String appBaseUrl;
    private final PayOS payOS;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PayOS payOS,
                          PaymentRepository paymentRepository,
                          @Lazy OrderService orderService,
                          @Value("${app.base-url}") String appBaseUrl) {
        this.payOS = payOS;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.appBaseUrl = appBaseUrl;
    }

    @Transactional
    public Payment createPaymentRecord(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(order.getFinalAmount()));
        payment.setStatus(PAYMENT_PENDING);
        return paymentRepository.save(payment);
    }

    @Transactional
    public String createPayOSLink(Payment payment) throws Exception {
        final long uniqueOrderCode = generateUniqueOrderCode();
        payment.setOrderCode(uniqueOrderCode);
        paymentRepository.save(payment);

        final String description = "Payment for order #" + payment.getOrder().getOrderId();
        final String returnUrl = buildCallbackUrl("/payment/callback/success");
        final String cancelUrl = buildCallbackUrl("/payment/callback/failed");

        List<PaymentLinkItem> items = new ArrayList<>();
        for (OrderDetail detail : payment.getOrder().getOrderDetails()) {
            items.add(PaymentLinkItem.builder()
                    .name(detail.getProduct().getProductName())
                    .quantity(detail.getQuantity())
                    .price((long) detail.getPrice())
                    .build());
        }

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(uniqueOrderCode)
                .amount(payment.getAmount().longValue())
                .description(description)
                .items(items)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .build();

        CreatePaymentLinkResponse payosResponse = payOS.paymentRequests().create(paymentData);
        payment.setGatewayPaymentId(payosResponse.getPaymentLinkId());
        paymentRepository.save(payment);

        return payosResponse.getCheckoutUrl();
    }

    @Transactional
    public String getOrRegeneratePaymentUrl(long orderId) throws Exception {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment info not found for order: " + orderId));

        if (!ORDER_PENDING_PAYMENT.equals(payment.getOrder().getStatus())) {
            throw new IllegalStateException("Order is not in Pending Payment state.");
        }

        try {
            if (payment.getOrderCode() != null) {
                PaymentLink currentLink = payOS.paymentRequests().get(payment.getOrderCode());
                if (PAYOS_STATUS_PAID.equals(currentLink.getStatus())) {
                    markPaymentSuccess(payment, null);
                    throw new IllegalStateException("Payment has already been completed for this order.");
                }
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw e;
            }
        }

        return createPayOSLink(payment);
    }

    @Transactional
    public void handleWebhook(Object body) throws Exception {
        WebhookData webhookData = payOS.webhooks().verify(body);
        long orderCode = webhookData.getOrderCode();
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("No information for order with orderCode: " + orderCode));
        Order order = payment.getOrder();
        String webhookType = webhookData.getCode();

        if (PAYOS_SUCCESS_CODE.equals(webhookType)) {
            markPaymentSuccess(payment, webhookData.getPaymentLinkId());
            payment.setRawPayload(webhookData.toString());
        } else {
            payment.setRawPayload(webhookData.toString());
            if (PAYMENT_PENDING.equals(payment.getStatus())) {
                orderService.cancelOrderFromPaymentId(payment.getPaymentId());
            }
        }
    }

    public PaymentInfoDTO getPaymentInfoByOrderId(long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("No information for order: " + orderId));
        return new PaymentInfoDTO(payment);
    }

    public PaymentLink queryTransaction(long orderCode) {
        try {
            return payOS.paymentRequests().get(orderCode);
        } catch (Exception e) {
            log.warn("Failed to query PayOS transaction for orderCode {}", orderCode, e);
            return null;
        }
    }

    @Transactional
    public boolean verifyPaymentStatus(long orderCode) throws Exception {
        PaymentLink transaction = queryTransaction(orderCode);
        if (transaction == null || !PAYOS_STATUS_PAID.equals(transaction.getStatus())) {
            return false;
        }

        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("No information for order with orderCode: " + orderCode));
        markPaymentSuccess(payment, null);
        return true;
    }

    @Transactional
    public void processFailedPayment(long orderCode) {
        try {
            Payment payment = paymentRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new EntityNotFoundException("Not found"));
            PaymentLink transaction = queryTransaction(orderCode);
            if (transaction != null && PAYOS_STATUS_PAID.equals(transaction.getStatus())) {
                markPaymentSuccess(payment, null);
                return;
            }
            orderService.cancelOrderFromPaymentId(payment.getPaymentId());
        } catch (Exception e) {
            log.warn("Failed to process failed payment for orderCode {}", orderCode, e);
        }
    }

    @Transactional
    public void cancelExpiredPayment(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("No payment found: " + paymentId));

        if (!PAYMENT_PENDING.equals(payment.getStatus())) {
            return;
        }

        Long orderCode = payment.getOrderCode();
        if (orderCode != null) {
            PaymentLink transaction = queryTransaction(orderCode);
            if (transaction != null && PAYOS_STATUS_PAID.equals(transaction.getStatus())) {
                markPaymentSuccess(payment, null);
                return;
            }
        }

        orderService.cancelOrderFromPaymentId(paymentId);
    }

    public PaymentInfoDTO getPaymentInfoSafe(long orderId) {
        try {
            return getPaymentInfoByOrderId(orderId);
        } catch (Exception e) {
            log.warn("Failed to get payment info for order {}", orderId, e);
            return null;
        }
    }

    private String buildCallbackUrl(String path) {
        return appBaseUrl.replaceAll("/+$", "") + path;
    }

    private long generateUniqueOrderCode() {
        long orderCode;
        do {
            orderCode = System.currentTimeMillis() * 1000 + ThreadLocalRandom.current().nextInt(1000);
        } while (paymentRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    private void markPaymentSuccess(Payment payment, String gatewayPaymentId) {
        if (PAYMENT_SUCCESS.equals(payment.getStatus())) {
            return;
        }

        Order order = payment.getOrder();
        payment.setStatus(PAYMENT_SUCCESS);
        if (gatewayPaymentId != null) {
            payment.setGatewayPaymentId(gatewayPaymentId);
        }
        order.setPaymentStatus(ORDER_PAYMENT_PAID);
        order.setStatus(ORDER_READY_TO_SHIP);
        order.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
