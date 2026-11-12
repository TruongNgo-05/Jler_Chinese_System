package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BookListResponseDTO {

  private Integer bookId;
  private String title;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String thumbnail;
  private Integer stock;
  private List<String> categoryNames;
}
