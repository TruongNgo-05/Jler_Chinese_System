package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Voucher {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true)
  private String code;

  private String type;

  @Column(precision = 15, scale = 2)
  private java.math.BigDecimal discount;

  @Column(precision = 15, scale = 2)
  private java.math.BigDecimal minOrder = java.math.BigDecimal.ZERO;

  @Column(precision = 15, scale = 2)
  private java.math.BigDecimal maxDiscount;

  private java.time.LocalDate expiredAt;
  private Integer maxUsage = 100;
  private Integer usedCount = 0;
  private Boolean active = true;
}
