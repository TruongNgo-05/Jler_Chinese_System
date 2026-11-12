package com.example.jler_chinese_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "Shipping_orders")
public class ShippingOder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shipping_order_id")
  private Integer shippingOrderId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", referencedColumnName = "order_id")
  private Order order;

  @Column(nullable = false, length = 15)
  private String phone;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String address;

  @Column(name = "full_name", nullable = false)
  private String fullName;
}
