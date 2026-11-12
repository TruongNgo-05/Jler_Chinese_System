package com.example.jler_chinese_system.dto;

import lombok.Data;

@Data
public class QuizCreateRequestDTO {

  private String title;
  private String quizType;
  private Integer passScore;
  private Integer timeLimit;
  private Integer maxAttempts;
  private Integer maxPauses;
}
