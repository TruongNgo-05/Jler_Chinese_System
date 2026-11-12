package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Integer> {
  Optional<Cart> findByUser_UserId(Integer userId); // Lấy giỏ hàng của User đang đăng nhập
}
