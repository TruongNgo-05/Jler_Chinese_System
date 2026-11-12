package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.QuizOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizOptionRepository
  extends JpaRepository<QuizOption, Integer>
{
  List<QuizOption> findByQuizquestion_QuizQuestionId(Integer questionId); // Lấy A, B, C, D
}
