package com.example.jler_chinese_system.utils;

import static com.example.jler_chinese_system.common.Checks.check;

import com.example.jler_chinese_system.entity.*;
import java.math.*;
import java.util.*;

public final class RequestValues {

  private RequestValues() {}

  public static String text(Map<String, Object> d, String k) {
    return Objects.toString(d.get(k), "").trim();
  }

  public static String required(Map<String, Object> d, String k) {
    String s = text(d, k);
    check(!s.isBlank() && s.length() <= 10000, "Thiếu hoặc quá dài: " + k);
    return s;
  }

  public static int number(Map<String, Object> d, String k, int fallback) {
    return d.get(k) == null || text(d, k).isBlank()
      ? fallback
      : Integer.parseInt(d.get(k).toString());
  }

  public static BigDecimal money(Map<String, Object> d, String k) {
    BigDecimal v = new BigDecimal(Objects.toString(d.get(k), "0"));
    check(
      v.signum() >= 0 && v.compareTo(new BigDecimal("999999999999")) <= 0,
      "Giá trị tiền không hợp lệ"
    );
    return v.setScale(2, RoundingMode.HALF_UP);
  }

  public static boolean bool(
    Map<String, Object> d,
    String k,
    boolean fallback
  ) {
    return d.containsKey(k)
      ? Boolean.parseBoolean(d.get(k).toString())
      : fallback;
  }

  public static String url(Map<String, Object> d, String key) {
    String s = text(d, key);
    check(
      s.isBlank() || s.startsWith("/uploads/") || s.matches("https?://[^\\s]+"),
      "URL không hợp lệ"
    );
    check(s.length() <= 1000, "URL quá dài");
    return s;
  }

  public static BigDecimal price(Book b) {
    return b.getDiscountPrice() != null ? b.getDiscountPrice() : b.getPrice();
  }

  public static BigDecimal price(Course c) {
    return c.getDiscountPrice() != null ? c.getDiscountPrice() : c.getPrice();
  }

  public static String canonicalType(String s) {
    return "book".equalsIgnoreCase(s)
      ? "Book"
      : "course".equalsIgnoreCase(s)
        ? "Course"
        : Objects.toString(s, "");
  }

  public static String itemType(String s) {
    s = canonicalType(s);
    check(Set.of("Book", "Course").contains(s), "Loại sản phẩm không hợp lệ");
    return s;
  }
}
