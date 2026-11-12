package com.example.jler_chinese_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
  uniqueConstraints = @UniqueConstraint(
    columnNames = { "userId", "itemId", "itemType" }
  )
)
public class Favorite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer userId;
  private Integer itemId;
  private String itemType;
}
