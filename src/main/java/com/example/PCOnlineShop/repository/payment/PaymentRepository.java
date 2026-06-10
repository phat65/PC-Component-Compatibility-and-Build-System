package com.example.PCOnlineShop.repository.payment;

import com.example.PCOnlineShop.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatusAndCreatedAtBefore(String status, Date timeLimit);

     Optional<Payment> findByOrder_OrderId(long orderId);

    Optional<Payment> findByOrderCode(long orderCode);

    boolean existsByOrderCode(long orderCode);
}
