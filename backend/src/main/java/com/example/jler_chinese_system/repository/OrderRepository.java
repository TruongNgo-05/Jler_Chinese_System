package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
  List<Order> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);
  Optional<Order> findByOrderCode(String orderCode); // Tra cứu vận đơn
}
