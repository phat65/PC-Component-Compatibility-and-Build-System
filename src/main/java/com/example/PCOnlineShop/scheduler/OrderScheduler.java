package com.example.PCOnlineShop.scheduler;

import com.example.PCOnlineShop.constant.PaymentStatus;
import com.example.PCOnlineShop.model.payment.Payment;
import com.example.PCOnlineShop.repository.payment.PaymentRepository;
import com.example.PCOnlineShop.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderScheduler {

    private static final int PAYMENT_TIMEOUT_MINUTES = 15;

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public OrderScheduler(PaymentRepository paymentRepository,
                          PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredPendingOrders() {
        Date paymentDeadline = Date.from(Instant.now().minus(PAYMENT_TIMEOUT_MINUTES, ChronoUnit.MINUTES));
        List<Payment> expiredPayments = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING,
                paymentDeadline
        );

        for (Payment payment : expiredPayments) {
            try {
                paymentService.cancelExpiredPayment(payment.getPaymentId());
            } catch (RuntimeException e) {
                log.warn("Unable to cancel expired payment {}", payment.getPaymentId(), e);
            }
        }
    }
}
