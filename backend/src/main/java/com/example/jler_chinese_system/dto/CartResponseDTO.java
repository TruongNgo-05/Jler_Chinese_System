package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDTO {

  private Integer cartId;
  private Integer totalItems;
  private BigDecimal totalAmount;

  private List<CartItemResponseDTO> items;
}
