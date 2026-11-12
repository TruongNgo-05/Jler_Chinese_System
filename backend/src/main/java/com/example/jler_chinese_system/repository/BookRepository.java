package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Book;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository
  extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book>
{
  @Query(
    "SELECT DISTINCT b FROM Book b " +
      "LEFT JOIN b.categories c " +
      "WHERE (:categoryId IS NULL OR c.categoryId = :categoryId) " +
      "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
      "AND (:maxPrice IS NULL OR b.price <= :maxPrice)"
  )
  List<Book> filterBooks(
    Integer categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice
  );

  @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.bookImages")
  List<Book> findAllWithImages();

  // Admin
  @EntityGraph(attributePaths = { "categories", "bookImages" })
  Page<Book> findAll(Pageable pageable);

  // Search
  @Override
  @EntityGraph(attributePaths = { "categories", "bookImages" })
  Page<Book> findAll(Specification<Book> spec, Pageable pageable);

  //export
  @Query(
    """
    SELECT DISTINCT b
    FROM Book b
    LEFT JOIN FETCH b.bookImages
    LEFT JOIN FETCH b.categories
    """
  )
  List<Book> findAllWithRelations();
}
