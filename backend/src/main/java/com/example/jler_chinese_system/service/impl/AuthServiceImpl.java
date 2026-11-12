package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.LoginRequestDTO;
import com.example.jler_chinese_system.dto.LoginResponseDTO;
import com.example.jler_chinese_system.entity.RefreshToken;
import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.repository.RefreshTokenRepository;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.security.JwtUtil;
import com.example.jler_chinese_system.service.*;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  private final JwtUtil jwtUtil;

  private final RefreshTokenRepository refreshTokenRepository;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public LoginResponseDTO login(LoginRequestDTO dto) {
    User user = userRepository
      .findByUserName(dto.getUserName())
      .orElseThrow(() -> new RuntimeException("User không tồn tại"));

    if (!passwordEncoder.matches(dto.getPassWord(), user.getPassWord())) {
      throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.UNAUTHORIZED,
        "Sai tài khoản hoặc mật khẩu"
      );
    }

    if (
      !Boolean.TRUE.equals(user.getStatus())
    ) throw new org.springframework.web.server.ResponseStatusException(
      org.springframework.http.HttpStatus.FORBIDDEN,
      "Tài khoản đã bị khóa"
    );
    String refreshToken = jwtUtil.generateRefreshToken(user);

    String accessToken = jwtUtil.generateAccessToken(user);

    RefreshToken tokenEntity = new RefreshToken();
    tokenEntity.setToken(refreshToken);
    tokenEntity.setUser(user);
    tokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
    refreshTokenRepository.deleteByUser(user);
    refreshTokenRepository.save(tokenEntity);

    LoginResponseDTO res = new LoginResponseDTO();
    res.setUserId(user.getUserId());
    res.setUserName(user.getUserName());
    res.setEmail(user.getEmail());
    res.setFullName(user.getFullName());
    res.setAccessToken(accessToken);
    res.setRefreshToken(refreshToken);

    return res;
  }

  @Override
  @Transactional
  public LoginResponseDTO refresh(String token) {
    RefreshToken refreshToken = refreshTokenRepository
      .findByToken(token)
      .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
      throw new RuntimeException("Refresh token hết hạn");
    }

    User user = refreshToken.getUser();
    if (
      !Boolean.TRUE.equals(user.getStatus())
    ) throw new org.springframework.web.server.ResponseStatusException(
      org.springframework.http.HttpStatus.FORBIDDEN,
      "Tài khoản đã bị khóa"
    );
    String newAccessToken = jwtUtil.generateAccessToken(user);

    LoginResponseDTO res = new LoginResponseDTO();
    res.setAccessToken(newAccessToken);
    res.setRefreshToken(token);
    res.setUserName(user.getUserName());
    return res;
  }

  @Override
  @Transactional
  public void logout(String token) {
    refreshTokenRepository.findByToken(token).ifPresent(t -> {
      t.getUser().setTokenVersion(t.getUser().getTokenVersion() + 1);
      refreshTokenRepository.delete(t);
    });
  }
}
