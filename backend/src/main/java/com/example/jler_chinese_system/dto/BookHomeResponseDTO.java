package com.example.jler_chinese_system.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({
  "id",
  "name",
  "categories",
  "image",
  "price",
  "originalPrice",
  "description",
})
public class BookHomeResponseDTO {

  private Integer id;
  private String name;
  private List<String> categories;
  private String image;
  private BigDecimal price;
  private BigDecimal originalPrice;
  private String description;
}
