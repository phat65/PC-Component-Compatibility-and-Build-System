package com.example.PCOnlineShop.repository.order;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.model.order.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product p WHERE od.order = :order")
    List<OrderDetail> findByOrder(@Param("order") Order order);

    @Query("SELECT DISTINCT od FROM OrderDetail od " +
            "JOIN FETCH od.order o " +
            "JOIN FETCH od.product p " +
            "LEFT JOIN FETCH p.categories " +
            "WHERE o.orderId = :orderId")
    List<OrderDetail> findByOrder_OrderIdWithAssociations(@Param("orderId") long orderId);

    boolean existsByOrder_Account_AccountIdAndProduct_ProductIdAndOrder_Status(
            Integer accountId, Integer productId, String status);

    boolean existsByProduct_ProductIdAndOrder_StatusIn(Integer productId, List<String> statuses);

    @Query("""
        SELECT COALESCE(SUM(od.quantity), 0)
        FROM OrderDetail od
        JOIN od.order o
        WHERE o.createdDate BETWEEN :startDate AND :endDate
          AND (o.paymentStatus = :paidStatus OR o.status = :completedStatus)
    """)
    Long sumRecognizedUnitsBetween(@Param("startDate") java.util.Date startDate,
                                   @Param("endDate") java.util.Date endDate,
                                   @Param("paidStatus") String paidStatus,
                                   @Param("completedStatus") String completedStatus);

    @Query("""
        SELECT p.productName, COALESCE(SUM(od.quantity), 0), COALESCE(SUM(od.quantity * od.price), 0)
        FROM OrderDetail od
        JOIN od.order o
        JOIN od.product p
        WHERE o.createdDate BETWEEN :startDate AND :endDate
          AND (o.paymentStatus = :paidStatus OR o.status = :completedStatus)
        GROUP BY p.productId, p.productName
        ORDER BY SUM(od.quantity) DESC, SUM(od.quantity * od.price) DESC
    """)
    List<Object[]> findTopRecognizedProductsBetween(@Param("startDate") java.util.Date startDate,
                                                    @Param("endDate") java.util.Date endDate,
                                                    @Param("paidStatus") String paidStatus,
                                                    @Param("completedStatus") String completedStatus,
                                                    Pageable pageable);
}
