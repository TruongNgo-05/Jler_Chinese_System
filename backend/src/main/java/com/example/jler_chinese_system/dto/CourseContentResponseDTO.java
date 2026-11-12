package com.example.jler_chinese_system.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CourseContentResponseDTO {

  private Integer courseContentId;
  private String contentType;
  private Integer orderIndex;
  private Boolean isPreview;

  private LessonResponseDTO lesson;
  private QuizResponseDTO quiz;
}
