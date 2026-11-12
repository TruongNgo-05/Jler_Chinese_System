package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.LoginRequestDTO;
import com.example.jler_chinese_system.dto.LoginResponseDTO;
import com.example.jler_chinese_system.dto.RegisterRequestDTO;
import com.example.jler_chinese_system.dto.RegisterResponseDTO;
import com.example.jler_chinese_system.entity.User;
import com.example.jler_chinese_system.service.AuthService;
import com.example.jler_chinese_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(
    @RequestBody @Valid RegisterRequestDTO dto
  ) {
    if (!dto.getPassWord().equals(dto.getConfirmPassword())) {
      throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.BAD_REQUEST,
        "Mật khẩu xác nhận không khớp"
      );
    }
    User user = new User();
    user.setUserName(dto.getUserName());
    user.setPassWord(dto.getPassWord());
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());

    user.setPhone(dto.getPhone());
    User savedUser = userService.register(user);
    RegisterResponseDTO reg = new RegisterResponseDTO();
    reg.setUserId(savedUser.getUserId());
    reg.setUserName(savedUser.getUserName());
    reg.setFullName(savedUser.getFullName());
    reg.setEmail(savedUser.getEmail());

    reg.setPhone(savedUser.getPhone());
    return ResponseEntity.ok(reg);
  }

  @PostMapping("/login")
  private ResponseEntity<LoginResponseDTO> login(
    @Valid @RequestBody LoginRequestDTO dto
  ) {
    return ResponseEntity.ok(authService.login(dto));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestParam String refreshToken) {
    authService.logout(refreshToken);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponseDTO> refresh(
    @RequestParam String refreshToken
  ) {
    return ResponseEntity.ok(authService.refresh(refreshToken));
  }
}
