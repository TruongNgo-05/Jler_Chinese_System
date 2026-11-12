package com.example.jler_chinese_system.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "id", "name", "price", "stock", "image", "category" })
public class BookInfomationDTO {

  // admin render data
  private Integer id;
  private String name;
  private BigDecimal price;
  private Integer stock;
  private String image;
  private List<String> categories;
}
