package com.example.jler_chinese_system.service;

import java.util.*;

public interface ShoppingService {
  Map<String, Object> addCart(Map<String, Object> d);

  Map<String, Object> changeCart(Integer id, Integer quantity);

  Map<String, Object> adjustCart(Integer id, int delta);

  Map<String, Object> clearCart();

  Map<String, Object> cart();

  Map<String, Object> quote(String code);

  Map<String, Object> checkout(Map<String, Object> d);

  Map<String, Object> orderDetail(Integer id);

  List<?> orders(boolean administrator);

  Map<String, Object> orderAction(
    Integer id,
    Map<String, Object> d,
    boolean administrator
  );

  List<?> favorites();

  void favorite(Map<String, Object> d);

  Map<String, Object> payment(Integer id);

  Map<String, Object> paymentOptions();
}
