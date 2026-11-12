package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.CartResponseDTO;

public interface CartService {
  void addToCart(Integer userId, Integer itemId, String itemType);

  void updateQuantity(Integer userId, Integer cartItemId, Integer quantity);

  void removeItem(Integer userId, Integer cartItemId);

  CartResponseDTO getCart(Integer userId);

  Integer getCartCount(Integer userId);
}
