package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
  Optional<CartItem> findByCart_CartIdAndItemIdAndItemType(
    Integer cartId,
    Integer itemId,
    String itemType
  ); // Check trùng món

  @Modifying
  @Query("DELETE FROM CartItem c WHERE c.itemId = :id")
  void deleteByItemId(@Param("id") Integer id);
}
