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
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Quiz_results")
public class QuizResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "quiz_results_id")
  private Integer quizResultsId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quizes_id")
  private Quiz quiz;

  @Column(name = "score")
  private Integer score;

  @Column(name = "submitted_at", nullable = false, updatable = false)
  @CreationTimestamp // Khớp: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  private LocalDateTime submittedAt;

  @Column(name = "total_questions")
  private Integer totalQuestions;

  @Column(name = "correct_answers")
  private Integer correctAnswers;

  @Column(name = "wrong_answers")
  private Integer wrongAnswers;

  @Column(name = "time_spent")
  private Integer timeSpent;

  @Column(name = "status")
  private String status;

  @OneToMany(
    mappedBy = "quizResult",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @OrderBy("orderIndex ASC")
  private List<QuizAttemptDetail> attemptDetails = new ArrayList<>();
}
