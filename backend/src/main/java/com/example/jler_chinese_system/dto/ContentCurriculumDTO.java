package com.example.jler_chinese_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentCurriculumDTO {

  private String contentType;
  private Integer orderIndex;
  private String title;
}
