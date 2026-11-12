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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Cart_items")
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cart_item_id")
  private Integer cartItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

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

  @Column(name = "item_id", nullable = false)
  private Integer itemId;

  @Column(name = "item_type", nullable = false, length = 20)
  private String itemType;

  private Integer quantity = 1;

  @Column(name = "price_at_purchase", precision = 15, scale = 2)
  private BigDecimal priceAtPurchase;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;
}
