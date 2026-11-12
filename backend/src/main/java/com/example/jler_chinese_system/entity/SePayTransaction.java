package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sepay_transactions")
public class SePayTransaction {

  @Id
  private Long id;

  private Integer orderId;
  private String orderCode;

  @Column(precision = 15, scale = 2)
  private BigDecimal amount;

  private String gateway;
  private String accountNumber;
  private String subAccount;

  @Column(length = 2000)
  private String content;

  private String referenceCode;
  private String status;
  private String reason;
  private LocalDateTime receivedAt = LocalDateTime.now();
}
