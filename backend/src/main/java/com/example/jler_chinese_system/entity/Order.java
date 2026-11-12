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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@Table(
  name = "orders",
  uniqueConstraints = @jakarta.persistence.UniqueConstraint(
    name = "uk_orders_user_checkout",
    columnNames = { "user_id", "checkout_key" }
  )
)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Integer orderId;

  @Column(name = "checkout_key", length = 64)
  private String checkoutKey;

  @Column(length = 64)
  private String requestSignature;

  private String status = "PENDING";
  private String paymentMethod = "COD";
  private Integer voucherId;

  @Column(precision = 15, scale = 2)
  private BigDecimal subtotal = BigDecimal.ZERO;

  @Column(precision = 15, scale = 2)
  private BigDecimal discount = BigDecimal.ZERO;

  @Column(precision = 15, scale = 2)
  private BigDecimal shippingFee = BigDecimal.ZERO;

  @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "order_code", unique = true, nullable = false, length = 50)
  private String orderCode;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "note", columnDefinition = "TEXT")
  private String note;

  @ManyToOne(
    cascade = {
      CascadeType.PERSIST,
      CascadeType.DETACH,
      CascadeType.REFRESH,
      CascadeType.MERGE,
    }
  )
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status")
  private PaymentStatus paymentStatus = PaymentStatus.pending;

  @Enumerated(EnumType.STRING)
  @Column(name = "shipping_status")
  private ShippingStatus shippingStatus = ShippingStatus.none;

  @Column(name = "transaction_id", length = 100)
  private String transactionId;

  @Column(name = "payment_at")
  private LocalDateTime paymentAt;

  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  @Column(name = "has_physical_item")
  private Boolean hasPhysicalItem = false;

  @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
  private ShippingOder shippingOrder;

  @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<OrderItem> oderItems = new ArrayList<>();

  public enum PaymentStatus {
    pending,
    paid,
    failed,
    expired,
  }

  public enum ShippingStatus {
    none,
    preparing,
    shipping,
    delivered,
    cancelled,
  }
}
