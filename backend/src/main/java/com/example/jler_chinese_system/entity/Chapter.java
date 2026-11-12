package com.example.jler_chinese_system.entity;

import com.example.jler_chinese_system.dto.CourseListResponseDTO;
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
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Chapters")
public class Chapter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chapter_id")
  private Integer chapterId;

  @Column(nullable = false)
  private String title;

  @Column(name = "order_index")
  private Integer orderIndex;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @OneToMany(
    mappedBy = "chapter",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<CourseContent> courseContents = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id") //
  private Course course;
}
