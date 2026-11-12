package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CourseListResponseDTO {

  private Integer courseId;
  private String title;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String thumbnailUrl;
  private String courseType;

  private String description;
}
