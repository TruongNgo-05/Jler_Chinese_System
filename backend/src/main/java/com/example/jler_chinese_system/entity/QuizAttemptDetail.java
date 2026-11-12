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
@Table(name = "Quiz_attempt_details")
public class QuizAttemptDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer quizAttemptDetailId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_result_id")
  private QuizResult quizResult;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_question_id")
  private QuizQuestion quizQuestion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "selected_option_id")
  private QuizOption selectedOption;

  @Column(name = "is_correct")
  private Boolean isCorrect;

  @Column(name = "answer_text", columnDefinition = "TEXT")
  private String answerText;

  @Column(name = "order_index")
  private Integer orderIndex;
}
