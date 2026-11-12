package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class QuizSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer userId;
  private Integer quizId;
  private java.time.LocalDateTime startedAt;
  private java.time.LocalDateTime expiresAt;
  private Boolean submitted = false;
}
