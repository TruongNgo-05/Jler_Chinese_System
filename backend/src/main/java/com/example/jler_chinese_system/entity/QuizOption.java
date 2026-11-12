package com.example.jler_chinese_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Quiz_options")
public class QuizOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "quiz_option_id")
  private Integer quizOptionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_question_id")
  private QuizQuestion quizquestion;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "is_correct")
  private Boolean isCorrect = false;
}
