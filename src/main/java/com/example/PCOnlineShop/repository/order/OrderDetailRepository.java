package com.example.PCOnlineShop.repository.order;
import java.util.List;

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
}
