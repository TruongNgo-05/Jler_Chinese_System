package com.example.jler_chinese_system.dto.filter;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookFilter {

  private List<Integer> categoryIds;
  private List<String> topics;

  private BigDecimal minPrice;
  private BigDecimal maxPrice;

  private int page = 0;
  private int size = 10;

  // price_asc | price_desc | newest
  private String sort;
}
