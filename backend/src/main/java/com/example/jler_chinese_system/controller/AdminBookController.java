package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.common.BaseResponse;
import com.example.jler_chinese_system.dto.*;
import com.example.jler_chinese_system.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

  private final BookService bookService;

  @GetMapping
  //danh sach
  public ResponseEntity<BaseResponse<Page<BookInfomationDTO>>> getAllBooks(
    Pageable pageable
  ) {
    return ResponseEntity.ok(
      new BaseResponse<>(bookService.getAllBooks(pageable), "get all book")
    );
  }

  //    export excel
  @GetMapping("/export-excel")
  public void exportBooks(HttpServletResponse response) throws IOException {
    response.setContentType(
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    response.setHeader(
      "Content-Disposition",
      "attachment; filename=books.xlsx"
    );

    bookService.exportBooksToExcel(response);
  }

  // theo id
  @GetMapping("/{id}")
  public ResponseEntity<BookDetailResponseDTO> getBookById(
    @PathVariable Integer id
  ) {
    return ResponseEntity.ok(bookService.getById(id));
  }

  @PostMapping
  public BookDetailResponseDTO create(
    @Valid @RequestBody BookRequestDTO request
  ) {
    return bookService.create(request);
  }

  @PutMapping("/{id}")
  public BookDetailResponseDTO update(
    @PathVariable Integer id,
    @Valid @RequestBody BookUpdateRequestDTO request
  ) {
    return bookService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    bookService.delete(id);
  }
}
