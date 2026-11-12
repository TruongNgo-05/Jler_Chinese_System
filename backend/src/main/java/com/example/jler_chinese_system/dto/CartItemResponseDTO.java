package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponseDTO {

  private Integer cartItemId;
  private Integer itemId;
  private String itemType;
  private String itemName;
  private String thumbnail;

  private Integer quantity;
  private BigDecimal price;
  private BigDecimal totalPrice;
}
