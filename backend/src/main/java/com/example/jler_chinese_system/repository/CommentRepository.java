package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Comment;
import java.awt.print.Pageable;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
  // Lấy bình luận của bài học, sắp xếp mới nhất (Phân trang Pageable)
}
