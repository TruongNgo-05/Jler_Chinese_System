package com.example.jler_chinese_system.dto;

import com.example.jler_chinese_system.entity.Course;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CourseRequestDTO {

  private String title;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String status;
  private String courseType; // "HSK Combo", "Giao tiếp"...
  private String description;
}
