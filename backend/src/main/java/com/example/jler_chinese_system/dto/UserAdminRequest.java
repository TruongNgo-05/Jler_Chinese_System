package com.example.jler_chinese_system.dto;

public class UserAdminRequest {

  private String username;
  private String password; // Admin có thể đặt mật khẩu tạm thời
  private String email;
  private String fullName;
  private String phone;
  private Integer roleId; // Admin chọn 1 (Admin), 2 (Student), hoặc 3 (Teacher)
  private Integer status; // Admin có thể tạo user ở trạng thái khóa (0) hoặc mở (1)
}
