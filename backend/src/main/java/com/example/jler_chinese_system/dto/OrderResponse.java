package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Data;

@Data
public class OrderResponse {

  private Integer orderId;
  private String orderCode;
  private BigDecimal totalAmount;
  private String paymentStatus;
  private String shippingStatus;
  private LocalDateTime createdAt;
  private List<CartItemResponseDTO> items;
}
