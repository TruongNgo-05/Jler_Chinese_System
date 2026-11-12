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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id")
  private Integer orderItemId;

  private String itemTitle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(name = "item_id")
  private Integer itemId;

  @Enumerated(EnumType.STRING) // Dùng Enum cho chuyên nghiệp
  @Column(name = "item_type")
  private ItemType itemType;

  @Column(
    name = "price_at_purchase",
    nullable = false,
    precision = 15,
    scale = 2
  )
  private BigDecimal priceAtPurchase;

  @Column(name = "quantity")
  private Integer quantity;

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
    referencedColumnName = "combo_id",
    insertable = false,
    updatable = false,
    foreignKey = @jakarta.persistence.ForeignKey(
      jakarta.persistence.ConstraintMode.NO_CONSTRAINT
    )
  )
  private Combo combo;

  public enum ItemType {
    Book,
    Course,
    Combo,
  }
}
