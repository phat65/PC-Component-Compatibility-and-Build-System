package com.example.PCOnlineShop.repository.payment;

import com.example.PCOnlineShop.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :timeLimit")
    List<Payment> findPendingPaymentsOlderThan(@Param("timeLimit") Date timeLimit);

     Optional<Payment> findByOrder_OrderId(long orderId);

    Optional<Payment> findByOrderCode(long orderCode);

    boolean existsByOrderCode(long orderCode);
}
