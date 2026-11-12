package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.CourseDetailResponseDTO;
import com.example.jler_chinese_system.dto.CourseListResponseDTO;
import com.example.jler_chinese_system.dto.CourseRequestDTO;
import com.example.jler_chinese_system.dto.CourseUpdateRequestDTO;
import com.example.jler_chinese_system.dto.CurriculumResponseDTO;
import com.example.jler_chinese_system.entity.Course;
import java.util.List;

public interface CourseService {
  List<CourseListResponseDTO> getAllCourses(String type);

  CourseDetailResponseDTO getCourseDetail(Integer id);
  CourseListResponseDTO update(Integer id, CourseUpdateRequestDTO dto);
  // Thêm hàm tạo mới cho Teacher
  CourseListResponseDTO createCourse(CourseRequestDTO dto, String thumbnailUrl);
  CurriculumResponseDTO getCurriculum(Integer courseId);

  void deleteCourse(Integer id);
}
