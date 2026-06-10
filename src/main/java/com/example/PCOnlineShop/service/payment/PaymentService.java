package com.example.PCOnlineShop.service.payment;

import com.example.PCOnlineShop.constant.OrderPaymentStatus;
import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.constant.PaymentStatus;
import com.example.PCOnlineShop.dto.payment.PaymentInfoDTO;
import com.example.PCOnlineShop.dto.order.CheckoutDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.order.OrderDetail;
import com.example.PCOnlineShop.model.payment.Payment;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.service.cart.CartService;
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
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class PaymentService {

    private static final PaymentLinkStatus PAYOS_STATUS_PAID = PaymentLinkStatus.PAID;
    private static final PaymentLinkStatus PAYOS_STATUS_CANCELLED = PaymentLinkStatus.CANCELLED;
    private static final PaymentLinkStatus PAYOS_STATUS_EXPIRED = PaymentLinkStatus.EXPIRED;
    private static final String PAYOS_SUCCESS_CODE = "00";

    private final String appBaseUrl;
    private final PayOS payOS;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final CartService cartService;

    public PaymentService(PayOS payOS,
                          PaymentRepository paymentRepository,
                          @Lazy OrderService orderService,
                          CartService cartService,
                          @Value("${app.base-url}") String appBaseUrl) {
        this.payOS = payOS;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.cartService = cartService;
        this.appBaseUrl = appBaseUrl;
    }

    public enum FailedPaymentResult {
        CANCELLED,
        PAID,
        PENDING
    }

    public String createCheckoutPaymentLink(Account account, CheckoutDTO checkoutDTO) {
        Payment payment = null;

        try {
            Order order = orderService.processCheckout(account, checkoutDTO);
            payment = createPaymentRecord(order);
            String checkoutUrl = createPayOSLink(payment);
            cartService.clearSelectedItems(account);
            return checkoutUrl;
        } catch (RuntimeException e) {
            if (payment != null) {
                cancelPendingPaymentAfterCheckoutFailure(payment.getPaymentId(), e);
            }
            throw e;
        }
    }

    @Transactional
    public Payment createPaymentRecord(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(order.getFinalAmount()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderCode(generateUniqueOrderCode());
        return paymentRepository.save(payment);
    }

    public String createPayOSLink(Payment payment) {
        if (payment.getOrderCode() == null) {
            payment.setOrderCode(generateUniqueOrderCode());
            payment = paymentRepository.save(payment);
        }

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
                .orderCode(payment.getOrderCode())
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
    public String getOrRegeneratePaymentUrl(long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment info not found for order: " + orderId));

        if (!OrderStatus.PENDING_PAYMENT.equals(payment.getOrder().getStatus())) {
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
        } catch (RuntimeException e) {
            if (e instanceof IllegalStateException) {
                throw e;
            }
            log.warn("Unable to verify existing PayOS link for order {}", orderId, e);
        }

        return createPayOSLink(payment);
    }

    @Transactional
    public void handleWebhook(Object body) {
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
            if (PaymentStatus.PENDING.equals(payment.getStatus())) {
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
        } catch (RuntimeException e) {
            log.warn("Failed to query PayOS transaction for orderCode {}", orderCode, e);
            return null;
        }
    }

    @Transactional
    public boolean verifyPaymentStatus(long orderCode) {
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
    public FailedPaymentResult processFailedPayment(long orderCode) {
        Payment payment = paymentRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Payment info not found for orderCode: " + orderCode));

        PaymentLink transaction = queryTransaction(orderCode);
        if (transaction == null) {
            log.warn("Skipped failed-payment cancellation because PayOS status could not be verified for orderCode {}", orderCode);
            return FailedPaymentResult.PENDING;
        }

        PaymentLinkStatus payOSStatus = transaction.getStatus();
        if (PAYOS_STATUS_PAID.equals(payOSStatus)) {
            markPaymentSuccess(payment, null);
            return FailedPaymentResult.PAID;
        }

        if (isPayOSCancellationStatus(payOSStatus)) {
            orderService.cancelOrderFromPaymentId(payment.getPaymentId());
            return FailedPaymentResult.CANCELLED;
        }

        log.info("Failed-payment callback did not cancel orderCode {} because PayOS status is {}", orderCode, payOSStatus);
        return FailedPaymentResult.PENDING;
    }

    @Transactional
    public void cancelExpiredPayment(long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("No payment found: " + paymentId));

        if (!PaymentStatus.PENDING.equals(payment.getStatus())) {
            return;
        }

        Long orderCode = payment.getOrderCode();
        if (orderCode != null) {
            PaymentLink transaction = queryTransaction(orderCode);
            if (transaction == null) {
                log.warn("Skipped expiring payment {} because PayOS status could not be verified", paymentId);
                return;
            }
            if (PAYOS_STATUS_PAID.equals(transaction.getStatus())) {
                markPaymentSuccess(payment, null);
                return;
            }
        }

        orderService.cancelOrderFromPaymentId(paymentId);
    }

    public PaymentInfoDTO getPaymentInfoSafe(long orderId) {
        try {
            return getPaymentInfoByOrderId(orderId);
        } catch (RuntimeException e) {
            log.warn("Failed to get payment info for order {}", orderId, e);
            return null;
        }
    }

    private String buildCallbackUrl(String path) {
        return appBaseUrl.replaceAll("/+$", "") + path;
    }

    private void cancelPendingPaymentAfterCheckoutFailure(long paymentId, RuntimeException cause) {
        try {
            orderService.cancelOrderFromPaymentId(paymentId);
        } catch (RuntimeException compensationException) {
            log.error("Failed to compensate checkout payment {} after payment-link creation error", paymentId, compensationException);
            cause.addSuppressed(compensationException);
        }
    }

    private boolean isPayOSCancellationStatus(PaymentLinkStatus status) {
        return PAYOS_STATUS_CANCELLED.equals(status)
                || PAYOS_STATUS_EXPIRED.equals(status);
    }

    private long generateUniqueOrderCode() {
        long orderCode;
        do {
            orderCode = System.currentTimeMillis() * 1000 + ThreadLocalRandom.current().nextInt(1000);
        } while (paymentRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    private void markPaymentSuccess(Payment payment, String gatewayPaymentId) {
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            return;
        }

        Order order = payment.getOrder();
        payment.setStatus(PaymentStatus.SUCCESS);
        if (gatewayPaymentId != null) {
            payment.setGatewayPaymentId(gatewayPaymentId);
        }
        order.setPaymentStatus(OrderPaymentStatus.PAID);
        order.setStatus(OrderStatus.READY_TO_SHIP);
        order.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}
