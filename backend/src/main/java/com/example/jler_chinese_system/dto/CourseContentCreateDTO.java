package com.example.jler_chinese_system.dto;

import lombok.Data;

@Data
public class CourseContentCreateDTO {

  private Integer id;
  private String contentType; // LESSON / QUIZ
  private Integer lessonId;
  private String lessonTitle;
  private Integer quizId;
  private String quizTitle;
  private Integer orderIndex;
  private Boolean isPreview;
}
