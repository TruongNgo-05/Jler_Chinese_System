package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.BookImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage, Integer> {
  List<BookImage> findByBook_BookId(Integer bookId); // Lấy bộ sưu tập ảnh cho 1 cuốn sách
}
