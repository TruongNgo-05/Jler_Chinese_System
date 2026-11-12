package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.CourseContentResponseDTO;
import com.example.jler_chinese_system.service.CourseContentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/course-contents")
@RequiredArgsConstructor
public class CourseContentController {

  private final CourseContentService courseContentService;

  // ADD LESSON vào chapter
  @PostMapping("/chapter/{chapterId}/lesson/{lessonId}")
  public CourseContentResponseDTO addLesson(
    @PathVariable Integer chapterId,
    @PathVariable Integer lessonId
  ) {
    return courseContentService.addLesson(chapterId, lessonId);
  }

  // ADD QUIZ vào chapter
  @PostMapping("/chapter/{chapterId}/quiz/{quizId}")
  public CourseContentResponseDTO addQuiz(
    @PathVariable Integer chapterId,
    @PathVariable Integer quizId
  ) {
    return courseContentService.addQuiz(chapterId, quizId);
  }

  // DELETE content
  @DeleteMapping("/{contentId}")
  public void delete(@PathVariable Integer contentId) {
    courseContentService.delete(contentId);
  }

  // GET content theo chapter
  @GetMapping("/chapter/{chapterId}")
  public List<CourseContentResponseDTO> getByChapter(
    @PathVariable Integer chapterId
  ) {
    return courseContentService.getByChapter(chapterId);
  }
}
