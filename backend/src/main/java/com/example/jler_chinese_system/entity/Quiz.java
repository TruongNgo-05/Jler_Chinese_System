package com.example.jler_chinese_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Quizes")
public class Quiz {

  @Column(nullable = false, columnDefinition = "boolean default true")
  private Boolean active = true;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "quizes_id")
  private Integer quizId;

  @Column(nullable = false)
  private String title;

  @Column(name = "quiz_type")
  private String quizType;

  @Column(name = "pass_score")
  private Integer passScore;

  @Column(name = "time_limit")
  private Integer timeLimit;

  // Thời gian (phút)
  @Column(name = "max_attempts")
  private Integer maxAttempts;

  @Column(name = "max_pauses") // Số lần cho phép làm bài
  private Integer maxPauses;

  // Số lần cho phép tạm dừng

  @OneToMany(
    mappedBy = "quiz",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<QuizQuestion> quizQuestions = new ArrayList<>();

  @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
  private List<QuizResult> quizResults = new ArrayList<>();

  @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
  private List<CourseContent> courseContents = new ArrayList<>();
}
