package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlatformPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String title;

  @Column(columnDefinition = "LONGTEXT")
  private String content;

  @Column(length = 1000)
  private String imageUrl;

  private String type;
  private Boolean active = true;
  private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
