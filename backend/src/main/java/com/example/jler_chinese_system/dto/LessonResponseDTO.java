package com.example.jler_chinese_system.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LessonResponseDTO {

  private Integer lessonId;
  private String title;
  private String videoUrl;
  private Integer duration;
  private Boolean isFree;
  private String attachmentUrl;
}
