package com.example.PCOnlineShop.service.payment;

import com.example.PCOnlineShop.constant.OrderPaymentStatus;
import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.constant.PaymentStatus;
import com.example.PCOnlineShop.dto.order.CheckoutDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.order.OrderDetail;
import com.example.PCOnlineShop.model.payment.Payment;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.service.cart.CartService;
import com.example.PCOnlineShop.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.service.blocking.v2.paymentRequests.PaymentRequestsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PayOS payOS;
    @Mock
    private PaymentRequestsService paymentRequestsService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private CartService cartService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(payOS, paymentRepository, orderService, cartService, "http://localhost:8080");
    }

    @Test
    void failedPaymentCallbackDoesNotCancelWhenPayOSStatusCannotBeVerified() {
        Payment payment = pendingPayment(11L, 123L);

        when(paymentRepository.findByOrderCode(123L)).thenReturn(Optional.of(payment));
        when(payOS.paymentRequests()).thenReturn(paymentRequestsService);
        when(paymentRequestsService.get(123L)).thenThrow(new RuntimeException("gateway unavailable"));

        PaymentService.FailedPaymentResult result = paymentService.processFailedPayment(123L);

        assertThat(result).isEqualTo(PaymentService.FailedPaymentResult.PENDING);
        verify(orderService, never()).cancelOrderFromPaymentId(anyLong());
    }

    @Test
    void failedPaymentCallbackCancelsOnlyAfterPayOSReportsCancelled() {
        Payment payment = pendingPayment(11L, 123L);

        when(paymentRepository.findByOrderCode(123L)).thenReturn(Optional.of(payment));
        when(payOS.paymentRequests()).thenReturn(paymentRequestsService);
        when(paymentRequestsService.get(123L)).thenReturn(paymentLink(PaymentLinkStatus.CANCELLED));

        PaymentService.FailedPaymentResult result = paymentService.processFailedPayment(123L);

        assertThat(result).isEqualTo(PaymentService.FailedPaymentResult.CANCELLED);
        verify(orderService).cancelOrderFromPaymentId(11L);
    }

    @Test
    void failedPaymentCallbackMarksPaymentSuccessWhenPayOSReportsPaid() {
        Payment payment = pendingPayment(11L, 123L);

        when(paymentRepository.findByOrderCode(123L)).thenReturn(Optional.of(payment));
        when(payOS.paymentRequests()).thenReturn(paymentRequestsService);
        when(paymentRequestsService.get(123L)).thenReturn(paymentLink(PaymentLinkStatus.PAID));

        PaymentService.FailedPaymentResult result = paymentService.processFailedPayment(123L);

        assertThat(result).isEqualTo(PaymentService.FailedPaymentResult.PAID);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getOrder().getStatus()).isEqualTo(OrderStatus.READY_TO_SHIP);
        assertThat(payment.getOrder().getPaymentStatus()).isEqualTo(OrderPaymentStatus.PAID);
        verify(paymentRepository).save(payment);
        verify(orderService, never()).cancelOrderFromPaymentId(anyLong());
    }

    @Test
    void checkoutPaymentCreationFailureCancelsPendingPaymentAndKeepsCartItems() throws Exception {
        Account account = new Account();
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        Order order = orderWithDetails();

        when(orderService.processCheckout(account, checkoutDTO)).thenReturn(order);
        when(paymentRepository.existsByOrderCode(anyLong())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(11L);
            return payment;
        });
        when(payOS.paymentRequests()).thenReturn(paymentRequestsService);
        when(paymentRequestsService.create(any(CreatePaymentLinkRequest.class)))
                .thenThrow(new RuntimeException("gateway unavailable"));

        assertThatThrownBy(() -> paymentService.createCheckoutPaymentLink(account, checkoutDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("gateway unavailable");

        verify(orderService).cancelOrderFromPaymentId(11L);
        verify(cartService, never()).clearSelectedItems(account);
    }

    private Payment pendingPayment(long paymentId, long orderCode) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setOrderCode(orderCode);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setOrder(orderWithDetails());
        return payment;
    }

    private Order orderWithDetails() {
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("CPU");
        product.setPrice(100);

        Order order = new Order();
        order.setOrderId(5L);
        order.setFinalAmount(100.0);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);
        detail.setProduct(product);
        detail.setQuantity(1);
        detail.setPrice(100);
        order.setOrderDetails(List.of(detail));
        return order;
    }

    private PaymentLink paymentLink(PaymentLinkStatus status) {
        PaymentLink link = new PaymentLink();
        link.setStatus(status);
        return link;
    }
}
