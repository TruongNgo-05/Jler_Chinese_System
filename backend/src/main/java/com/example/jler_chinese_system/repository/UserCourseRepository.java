package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository
  extends JpaRepository<UserCourse, Integer>
{
  boolean existsByUser_UserIdAndCourse_CourseId(
    Integer userId,
    Integer courseId
  ); // Check học viên đã mua khóa này chưa
}
