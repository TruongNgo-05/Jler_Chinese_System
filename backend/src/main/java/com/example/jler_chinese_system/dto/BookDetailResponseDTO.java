package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BookDetailResponseDTO {

  private Integer bookId;
  private String title;
  private String description;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private Integer stock;
  private List<String> imageUrls;
  private List<CategorySummaryDTO> categories;

  @Data
  public static class CategorySummaryDTO {

    private Integer categoryId;
    private String nameCategory;
  }
}
