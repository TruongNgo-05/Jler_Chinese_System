package com.example.jler_chinese_system.dto;

import com.example.jler_chinese_system.entity.Course;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CourseUpdateRequestDTO {

  private String title;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String thumbnailUrl;
  private String description;
  private String courseType;
}
