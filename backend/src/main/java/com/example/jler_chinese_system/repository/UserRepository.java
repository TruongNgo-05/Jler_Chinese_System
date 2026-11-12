package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUserName(String userName);

  boolean existsByEmail(String email); // Kiểm tra Email đã tồn tại chưa khi đăng ký
  boolean existsByUserName(String userName); // Kiểm tra Email đã tồn tại chưa khi đăng ký
}
