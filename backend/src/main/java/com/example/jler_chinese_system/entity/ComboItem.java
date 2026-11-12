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
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Combo_items")
public class ComboItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "combo_item_id")
  private Integer comboItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "combo_id", nullable = false)
  private Combo combo;

  @Column(name = "item_id", nullable = false)
  private Integer itemId;

  @Column(name = "item_type", nullable = false, length = 20)
  private String itemType;

  private Integer quantity = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "item_id",
    referencedColumnName = "book_id",
    insertable = false,
    updatable = false,
    foreignKey = @jakarta.persistence.ForeignKey(
      jakarta.persistence.ConstraintMode.NO_CONSTRAINT
    )
  )
  private Book book;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "item_id",
    referencedColumnName = "course_id",
    insertable = false,
    updatable = false,
    foreignKey = @jakarta.persistence.ForeignKey(
      jakarta.persistence.ConstraintMode.NO_CONSTRAINT
    )
  )
  private Course course;
}
