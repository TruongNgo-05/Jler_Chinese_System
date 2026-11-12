package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.*;
import com.example.jler_chinese_system.dto.filter.BookFilter;
import com.example.jler_chinese_system.entity.Book;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
  // admin
  BookDetailResponseDTO create(BookRequestDTO request);

  BookDetailResponseDTO update(Integer id, BookUpdateRequestDTO request);

  void delete(Integer id);

  Page<BookInfomationDTO> getAllBooks(Pageable pageable);

  void exportBooksToExcel(HttpServletResponse response) throws IOException;

  BookDetailResponseDTO getById(Integer id);
  //user

  List<BookListResponseDTO> getAllFiltered(
    Integer catId,
    BigDecimal min,
    BigDecimal max
  );

  BookDetailResponseDTO getDetail(Integer id);

  Page<Book> searchBooks(BookFilter bookFilter);

  List<BookHomeResponseDTO> getHomeBooks();
}
