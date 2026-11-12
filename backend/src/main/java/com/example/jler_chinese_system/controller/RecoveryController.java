package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.RecoveryService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RecoveryController {

  private final RecoveryService s;

  @PostMapping("/forgot-password")
  public Object forgot(@RequestBody Map<String, String> d) {
    try {
      s.forgot(d.getOrDefault("email", ""));
    } catch (org.springframework.mail.MailException e) {
      return ResponseEntity.status(503).body(
        Map.of("message", "Chưa gửi được email, vui lòng thử lại sau")
      );
    }
    return Map.of(
      "message",
      "Nếu email đã đăng ký, hướng dẫn đặt lại mật khẩu sẽ được gửi tới bạn."
    );
  }

  @PostMapping("/reset-password")
  public Object reset(@RequestBody Map<String, String> d) {
    s.reset(
      d.getOrDefault("token", ""),
      d.getOrDefault("password", ""),
      d.getOrDefault("confirmPassword", "")
    );
    return Map.of("message", "Đã đặt lại mật khẩu");
  }
}
