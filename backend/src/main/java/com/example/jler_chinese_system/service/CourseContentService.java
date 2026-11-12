package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.CourseContentResponseDTO;
import java.util.List;

public interface CourseContentService {
  CourseContentResponseDTO addLesson(Integer chapterId, Integer lessonId);

  CourseContentResponseDTO addQuiz(Integer chapterId, Integer quizId);

  void delete(Integer contentId);

  List<CourseContentResponseDTO> getByChapter(Integer chapterId);
}
