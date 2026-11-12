package com.example.jler_chinese_system.dto;

import lombok.Data;

@Data
public class OrderRequest {

  private String fullName; // Tên người nhận (có thể khác tên chủ tài khoản)
  private String address; // Địa chỉ giao hàng
  private String phone; // Số điện thoại nhận hàng
  private String note; // Ghi chú (Ví dụ: Giao giờ hành chính)
  private String paymentMethod; // VNPAY, MoMo, hoặc COD (Thanh toán khi nhận hàng)
  private Integer cartId;
}
