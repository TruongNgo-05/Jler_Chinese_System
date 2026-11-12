package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.CourseContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseContentRepository
  extends JpaRepository<CourseContent, Integer>
{
  List<CourseContent> findByChapterChapterIdOrderByOrderIndexAsc(
    Integer chapterId
  ); // Sắp xếp Video và Quiz trong chương

  @Query(
    "SELECT COALESCE(MAX(c.orderIndex),0) FROM CourseContent c WHERE c.chapter.chapterId = :chapterId"
  )
  Integer findMaxOrderIndex(@Param("chapterId") Integer chapterId);
}
