package com.example.jler_chinese_system.dto;

import lombok.Data;

@Data
public class LessonCreateRequestDTO {

  private String title;
  private String videoUrl;
  private Integer duration;
  private Boolean isFree;
  private String attachmentUrl;
}
