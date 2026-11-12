package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.LoginRequestDTO;
import com.example.jler_chinese_system.dto.LoginResponseDTO;

public interface AuthService {
  LoginResponseDTO login(LoginRequestDTO dto);

  LoginResponseDTO refresh(String token);

  void logout(String token);
}
