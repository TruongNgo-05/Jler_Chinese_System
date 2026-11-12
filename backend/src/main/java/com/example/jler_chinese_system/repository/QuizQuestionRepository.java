package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.QuizQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizQuestionRepository
  extends JpaRepository<QuizQuestion, Integer>
{
  List<QuizQuestion> findByQuiz_QuizIdOrderByOrderIndexAsc(Integer quizId); // Lấy bộ câu hỏi
}
