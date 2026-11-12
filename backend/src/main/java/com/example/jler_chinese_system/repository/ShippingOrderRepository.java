package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.ShippingOder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingOrderRepository
  extends JpaRepository<ShippingOder, Integer>
{
  Optional<ShippingOder> findByOrder_OrderId(Integer orderId); // Lấy địa chỉ giao hàng
}
