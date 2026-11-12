package com.example.jler_chinese_system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "courses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id")
  private Integer courseId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(precision = 15, scale = 2)
  private BigDecimal price;

  @Column(name = "discount_price", precision = 15, scale = 2)
  private BigDecimal discountPrice;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "thumbnail_url", length = 1000)
  private String thumbnailUrl;

  @Column(name = "total_lesson")
  private Integer totalLesson;

  @Column(name = "status")
  private String status;

  @Column(name = "total_duration")
  private Integer totalDuration;

  @Column(name = "course_type")
  private String courseType; // Để lưu "HSK Combo", "Giao tiếp", v.v.

  @OneToMany(
    mappedBy = "course",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<Chapter> chapters = new ArrayList<>();

  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
  private List<UserCourse> userCourses = new ArrayList<>();

  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
  private List<CartItem> cartItems = new ArrayList<>();

  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
  private List<OrderItem> orderItems = new ArrayList<>();

  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
  private List<ComboItem> comboItems = new ArrayList<>();
}
