package com.example.jler_chinese_system.dto;

import lombok.Data;

@Data
public class UserResponse {

  private Integer userId;
  private String username;
  private String email;
  private String fullName;
  private String phone;
  private String roleName; // Chuyển từ Role Entity sang String
  private Integer status;
}
