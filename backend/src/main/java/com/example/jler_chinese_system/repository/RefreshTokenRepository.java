package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.RefreshToken;
import com.example.jler_chinese_system.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RefreshTokenRepository
  extends JpaRepository<RefreshToken, Long>
{
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  @Transactional
  void deleteByUser(User user);
}
