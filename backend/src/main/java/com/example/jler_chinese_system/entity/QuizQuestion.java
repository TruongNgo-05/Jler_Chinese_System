package com.example.jler_chinese_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Quiz_questions")
public class QuizQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "quiz_question_id")
  private Integer quizQuestionId;

  @Column(name = "question_type")
  private String questionType;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "audio_url", length = 1000)
  private String audioUrl;

  @OneToMany(
    mappedBy = "quizquestion",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  @OrderBy("quizOptionId ASC") // Tự động sắp xếp A, B, C, D khi lấy dữ liệu
  private List<QuizOption> quizOptions = new ArrayList<>();

  @Column(name = "order_index")
  private Integer orderIndex;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quizId")
  private Quiz quiz;
}
