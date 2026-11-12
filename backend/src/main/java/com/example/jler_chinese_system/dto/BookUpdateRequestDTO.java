package com.example.jler_chinese_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class BookUpdateRequestDTO {

  @NotBlank
  private String title;

  @NotBlank
  private String description;

  @NotNull
  @Positive
  private BigDecimal price;

  private BigDecimal discountPrice;

  @NotNull
  @Min(0)
  private Integer stock;

  private List<Integer> categoryIds;

  private List<String> imageUrls;
}
