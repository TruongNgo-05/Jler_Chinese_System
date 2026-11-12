package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.UserLessonProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLessonProgressRepository
  extends JpaRepository<UserLessonProgress, Integer>
{
  Optional<UserLessonProgress> findByUser_UserIdAndLesson_LessonId(
    Integer userId,
    Integer lessonId
  );
}
