package com.example.jler_chinese_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookRequestDTO {

  @NotBlank
  private String title;

  @NotBlank
  private String description;

  @NotNull
  @Positive
  private BigDecimal price;

  private BigDecimal discountPrice;

  @NotNull
  @Positive
  private Integer stock;

  @NotEmpty
  private List<Integer> categoryIds;

  private List<String> imageUrls;
}
