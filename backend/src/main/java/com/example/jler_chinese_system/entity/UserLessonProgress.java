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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "User_lesson_progress")
public class UserLessonProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_lesson_progressId")
  private Integer userLessonProgressId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Column(name = "is_completed")
  private Boolean isCompleted;

  @Column(name = "last_position")
  private Integer lastPosition = 0;

  @Column(name = "last_accessed_at")
  @UpdateTimestamp // Tự động cập nhật mỗi khi học viên bấm vào xem lại
  private LocalDateTime lastAccessedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;
}
