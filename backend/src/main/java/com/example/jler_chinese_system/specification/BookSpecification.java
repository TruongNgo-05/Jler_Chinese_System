package com.example.jler_chinese_system.specification;

import com.example.jler_chinese_system.entity.Book;
import com.example.jler_chinese_system.entity.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

  public static Specification<Book> hasCategories(List<Integer> categoryIds) {
    return (root, query, cb) -> {
      if (categoryIds == null || categoryIds.isEmpty()) return null;

      query.distinct(true);

      Join<Book, Category> join = root.join("categories", JoinType.LEFT);

      return join.get("categoryId").in(categoryIds);
    };
  }

  public static Specification<Book> hasTopics(List<String> topics) {
    return (root, query, cb) -> {
      if (topics == null || topics.isEmpty()) return null;

      query.distinct(true);

      Join<Book, Category> join = root.join("categories", JoinType.LEFT);

      return join.get("nameCategory").in(topics);
    };
  }

  public static Specification<Book> priceBetween(
    BigDecimal min,
    BigDecimal max
  ) {
    return (root, query, cb) -> {
      if (min != null && max != null) return cb.between(
        root.get("price"),
        min,
        max
      );

      if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);

      if (max != null) return cb.lessThanOrEqualTo(root.get("price"), max);

      return null;
    };
  }
}
