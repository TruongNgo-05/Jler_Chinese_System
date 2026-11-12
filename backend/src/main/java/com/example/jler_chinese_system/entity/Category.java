package com.example.jler_chinese_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Categories")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Integer categoryId;

  @Column(name = "name_category", nullable = false, length = 100)
  private String nameCategory;

  // Xóa đoạn ManyToOne cũ, thay bằng đoạn này:
  @ManyToMany(mappedBy = "categories")
  @JsonIgnore
  private List<Book> books = new ArrayList<>();
}
