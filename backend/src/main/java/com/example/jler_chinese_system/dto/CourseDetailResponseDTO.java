package com.example.jler_chinese_system.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseDetailResponseDTO {

  private Integer courseId;
  private String title;
  private BigDecimal price;
  private BigDecimal discountPrice;
  private String description;
  private String courseType;

  private String thumbnailUrl;
  private List<ChapterResponseDTO> chapters;
}
