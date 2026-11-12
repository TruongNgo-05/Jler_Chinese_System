package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordReset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, length = 64)
  private String tokenHash;

  private Integer userId;
  private java.time.LocalDateTime expiresAt;
  private Boolean used = false;
}
