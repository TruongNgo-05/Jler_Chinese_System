package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.QuizResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizResultRepository
  extends JpaRepository<QuizResult, Integer>
{
  List<QuizResult> findByUser_UserIdOrderBySubmittedAtDesc(Integer userId); // Xem lịch sử điểm thi
}
