package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.common.BaseResponse;
import com.example.jler_chinese_system.dto.BookDetailResponseDTO;
import com.example.jler_chinese_system.dto.BookHomeResponseDTO;
import com.example.jler_chinese_system.dto.BookListResponseDTO;
import com.example.jler_chinese_system.dto.filter.BookFilter;
import com.example.jler_chinese_system.entity.Book;
import com.example.jler_chinese_system.service.BookService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public List<BookListResponseDTO> getStore(
    @RequestParam(required = false) Integer categoryId,
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(required = false) BigDecimal maxPrice
  ) {
    return bookService.getAllFiltered(categoryId, minPrice, maxPrice);
  }

  @GetMapping("/{id}")
  public BookDetailResponseDTO getById(@PathVariable Integer id) {
    return bookService.getDetail(id);
  }

  // search
  @PostMapping("/search")
  public ResponseEntity<Page<Book>> searchBooks(
    @RequestBody BookFilter bookFilter
  ) {
    return ResponseEntity.ok(bookService.searchBooks(bookFilter));
  }

  // gendata
  @GetMapping("/home")
  public List<BookHomeResponseDTO> getHomeBooks() {
    return bookService.getHomeBooks();
  }
}
