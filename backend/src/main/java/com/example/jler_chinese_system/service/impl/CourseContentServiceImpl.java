package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.CourseContentResponseDTO;
import com.example.jler_chinese_system.dto.LessonResponseDTO;
import com.example.jler_chinese_system.dto.QuizResponseDTO;
import com.example.jler_chinese_system.entity.Chapter;
import com.example.jler_chinese_system.entity.CourseContent;
import com.example.jler_chinese_system.entity.Lesson;
import com.example.jler_chinese_system.entity.Quiz;
import com.example.jler_chinese_system.repository.ChapterRepository;
import com.example.jler_chinese_system.repository.CourseContentRepository;
import com.example.jler_chinese_system.repository.LessonRepository;
import com.example.jler_chinese_system.repository.QuizRepository;
import com.example.jler_chinese_system.service.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseContentServiceImpl implements CourseContentService {

  private final CourseContentRepository repository;
  private final ChapterRepository chapterRepository;
  private final LessonRepository lessonRepository;
  private final QuizRepository quizRepository;

  @Override
  public CourseContentResponseDTO addLesson(
    Integer chapterId,
    Integer lessonId
  ) {
    Chapter chapter = chapterRepository
      .findById(chapterId)
      .orElseThrow(() -> new RuntimeException("Chapter not found"));

    Lesson lesson = lessonRepository
      .findById(lessonId)
      .orElseThrow(() -> new RuntimeException("Lesson not found"));

    Integer maxOrder = repository.findMaxOrderIndex(chapterId);

    CourseContent content = new CourseContent();
    content.setChapter(chapter);
    content.setLesson(lesson);
    content.setContentType("LESSON");
    content.setOrderIndex(maxOrder + 1);
    content.setIsPreview(false);

    repository.save(content);

    return mapToDTO(content);
  }

  @Override
  public CourseContentResponseDTO addQuiz(Integer chapterId, Integer quizId) {
    Chapter chapter = chapterRepository
      .findById(chapterId)
      .orElseThrow(() -> new RuntimeException("Chapter not found"));

    Quiz quiz = quizRepository
      .findById(quizId)
      .orElseThrow(() -> new RuntimeException("Quiz not found"));

    Integer maxOrder = repository.findMaxOrderIndex(chapterId);

    CourseContent content = new CourseContent();
    content.setChapter(chapter);
    content.setQuiz(quiz);
    content.setContentType("QUIZ");
    content.setOrderIndex(maxOrder + 1);
    content.setIsPreview(false);

    repository.save(content);

    return mapToDTO(content);
  }

  @Override
  public void delete(Integer contentId) {
    repository.deleteById(contentId);
  }

  @Override
  public List<CourseContentResponseDTO> getByChapter(Integer chapterId) {
    return repository
      .findByChapterChapterIdOrderByOrderIndexAsc(chapterId)
      .stream()
      .map(this::mapToDTO)
      .toList();
  }

  private CourseContentResponseDTO mapToDTO(CourseContent content) {
    return CourseContentResponseDTO.builder()
      .courseContentId(content.getCourseContentId())
      .contentType(content.getContentType())
      .orderIndex(content.getOrderIndex())
      .isPreview(content.getIsPreview())
      .lesson(
        content.getLesson() != null
          ? LessonResponseDTO.builder()
              .lessonId(content.getLesson().getLessonId())
              .title(content.getLesson().getTitle())
              .videoUrl(content.getLesson().getVideoUrl())
              .duration(content.getLesson().getDuration())
              .isFree(content.getLesson().getIsFree())
              .attachmentUrl(content.getLesson().getAttachmentUrl())
              .build()
          : null
      )
      .quiz(
        content.getQuiz() != null
          ? QuizResponseDTO.builder()
              .quizId(content.getQuiz().getQuizId())
              .title(content.getQuiz().getTitle())
              .quizType(content.getQuiz().getQuizType())
              .passScore(content.getQuiz().getPassScore())
              .timeLimit(content.getQuiz().getTimeLimit())
              .maxAttempts(content.getQuiz().getMaxAttempts())
              .maxPauses(content.getQuiz().getMaxPauses())
              .build()
          : null
      )
      .build();
  }
}
