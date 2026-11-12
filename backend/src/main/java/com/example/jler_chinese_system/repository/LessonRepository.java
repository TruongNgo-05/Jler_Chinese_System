package com.example.jler_chinese_system.repository;

import com.example.jler_chinese_system.entity.Course;
import com.example.jler_chinese_system.entity.Lesson;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {}
