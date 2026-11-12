package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.dto.CourseListResponseDTO;
import com.example.jler_chinese_system.entity.Course;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
  List<Course> findByTitleContainingIgnoreCase(String title);

  List<Course> findByCourseType(String type);
}
