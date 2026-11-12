package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Integer bookId;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(precision = 15, scale = 2)
  private BigDecimal price;

  @Column(name = "discount_price", precision = 15, scale = 2)
  private BigDecimal discountPrice;

  private Integer stock;

  // ================= CATEGORY =================
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "book_category_mapping",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "category_id")
  )
  private Set<Category> categories = new HashSet<>();

  // ================= IMAGES =================
  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BookImage> bookImages = new HashSet<>();

  // ================= COMBO =================
  @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
  private Set<ComboItem> comboItems = new HashSet<>();

  // ================= ORDER =================
  @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
  private Set<OrderItem> orderItems = new HashSet<>();
}
