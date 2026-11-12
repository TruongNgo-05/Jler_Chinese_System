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
@Table(name = "Flashcards")
public class Flashcard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "flashcard_id")
  private Integer flashcardId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id") // Khóa ngoại kết nối với bảng Lesson
  private Lesson lesson;

  @Column(name = "front_text", columnDefinition = "TEXT", nullable = false)
  private String fronttext;

  @Column(name = "back_text", columnDefinition = "TEXT", nullable = false)
  private String backtext;

  @Column(name = "image_url", length = 1000)
  private String imageUrl;
}
