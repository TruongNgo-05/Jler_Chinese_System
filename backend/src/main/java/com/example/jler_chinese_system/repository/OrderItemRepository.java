package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
  List<OrderItem> findByOrder_OrderId(Integer orderId); // Chi tiết các món đã mua

  @Modifying
  @Query("DELETE FROM OrderItem o WHERE o.itemId = :id")
  void deleteByItemId(@Param("id") Integer id);
}
