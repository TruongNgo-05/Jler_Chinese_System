package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class ShoppingController {

  private final ShoppingService shoppingService;

  @GetMapping("/cart")
  public Object cart() {
    return shoppingService.cart();
  }

  @PostMapping("/cart")
  public Object add(@RequestBody Map<String, Object> d) {
    return shoppingService.addCart(d);
  }

  @PutMapping("/cart/{id}")
  public Object qty(
    @PathVariable Integer id,
    @RequestBody Map<String, Integer> d
  ) {
    return shoppingService.changeCart(id, d.getOrDefault("quantity", -1));
  }

  @DeleteMapping("/cart/{id}")
  public Object remove(@PathVariable Integer id) {
    return shoppingService.changeCart(id, 0);
  }

  @GetMapping("/cart/quote")
  public Object quote(@RequestParam(defaultValue = "") String code) {
    return shoppingService.quote(code);
  }

  @GetMapping("/orders")
  public Object orders() {
    return shoppingService.orders(false);
  }

  @PostMapping("/orders")
  public Object checkout(@RequestBody Map<String, Object> d) {
    return shoppingService.checkout(d);
  }

  @PostMapping("/orders/{id}/actions")
  public Object cancel(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    return shoppingService.orderAction(id, d, false);
  }

  @GetMapping("/favorites")
  public Object favorites() {
    return shoppingService.favorites();
  }

  @PostMapping("/favorites")
  public void favorite(@RequestBody Map<String, Object> d) {
    shoppingService.favorite(d);
  }

  @PostMapping("/admin/orders/{id}/actions")
  public Object action(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    return shoppingService.orderAction(id, d, true);
  }

  @GetMapping("/payment-options")
  public Object paymentOptions() {
    return shoppingService.paymentOptions();
  }

  @GetMapping("/orders/{id}/payment")
  public Object payment(@PathVariable Integer id) {
    return shoppingService.payment(id);
  }

  @PostMapping("/cart/{id}/adjust")
  public Object adjust(
    @PathVariable Integer id,
    @RequestBody Map<String, Integer> d
  ) {
    return shoppingService.adjustCart(id, d.getOrDefault("delta", 0));
  }

  @DeleteMapping("/cart")
  public Object clearCart() {
    return shoppingService.clearCart();
  }

  @GetMapping("/orders/{id}")
  public Object order(@PathVariable Integer id) {
    return shoppingService.orderDetail(id);
  }
}
