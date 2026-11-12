package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Chapter;
import com.example.jler_chinese_system.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
  List<Chapter> findByCourseCourseIdOrderByOrderIndexAsc(Integer courseId);

  @Query(
    "SELECT COALESCE(MAX(c.orderIndex),0) FROM Chapter c WHERE c.course.courseId = :courseId"
  )
  Integer findMaxOrderIndex(@Param("courseId") Integer courseId);
}
