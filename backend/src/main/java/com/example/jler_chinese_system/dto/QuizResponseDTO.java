package com.example.jler_chinese_system.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuizResponseDTO {

  private Integer quizId;
  private String title;
  private String quizType;
  private Integer passScore;
  private Integer timeLimit;
  private Integer maxAttempts;
  private Integer maxPauses;
}
