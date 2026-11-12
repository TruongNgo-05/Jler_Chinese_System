package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Flashcard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
  List<Flashcard> findByLesson_LessonId(Integer lessonId); // Lấy bộ từ vựng theo bài học
}
