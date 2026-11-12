package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.ChapterCurriculumDTO;
import com.example.jler_chinese_system.dto.ChapterResponseDTO;
import com.example.jler_chinese_system.dto.ContentCurriculumDTO;
import com.example.jler_chinese_system.dto.CourseContentResponseDTO;
import com.example.jler_chinese_system.dto.CourseDetailResponseDTO;
import com.example.jler_chinese_system.dto.CourseListResponseDTO;
import com.example.jler_chinese_system.dto.CourseRequestDTO;
import com.example.jler_chinese_system.dto.CourseUpdateRequestDTO;
import com.example.jler_chinese_system.dto.CurriculumResponseDTO;
import com.example.jler_chinese_system.dto.LessonResponseDTO;
import com.example.jler_chinese_system.dto.QuizResponseDTO;
import com.example.jler_chinese_system.entity.Chapter;
import com.example.jler_chinese_system.entity.Course;
import com.example.jler_chinese_system.entity.CourseContent;
import com.example.jler_chinese_system.entity.Lesson;
import com.example.jler_chinese_system.repository.ChapterRepository;
import com.example.jler_chinese_system.repository.CourseContentRepository;
import com.example.jler_chinese_system.repository.CourseRepository;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.service.CourseService; // Thay đổi đúng theo package của bạn
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;

  private final ChapterRepository chapterRepository;
  private final CourseContentRepository courseContentRepository;

  @Override
  public List<CourseListResponseDTO> getAllCourses(String type) {
    List<Course> courses;

    if (type == null || type.isBlank()) {
      courses = courseRepository.findAll();
    } else {
      courses = courseRepository.findByCourseType(type);
    }

    return courses.stream().map(this::mapToListDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CourseDetailResponseDTO getCourseDetail(Integer id) {
    Course course = courseRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Course not found"));

    List<Chapter> chapters =
      chapterRepository.findByCourseCourseIdOrderByOrderIndexAsc(id);

    List<ChapterResponseDTO> chapterDTOs = chapters
      .stream()
      .map(chapter -> {
        List<CourseContentResponseDTO> contents = courseContentRepository
          .findByChapterChapterIdOrderByOrderIndexAsc(chapter.getChapterId())
          .stream()
          .map(this::mapContentToDTO)
          .toList();

        return ChapterResponseDTO.builder()
          .chapterId(chapter.getChapterId())
          .title(chapter.getTitle())
          .orderIndex(chapter.getOrderIndex())
          .isActive(chapter.getIsActive())
          .contents(contents)
          .build();
      })
      .toList();

    return CourseDetailResponseDTO.builder()
      .courseId(course.getCourseId())
      .title(course.getTitle())
      .description(course.getDescription())
      .price(course.getPrice())
      .discountPrice(course.getDiscountPrice())
      .thumbnailUrl(course.getThumbnailUrl())
      .chapters(chapterDTOs)
      .build();
  }

  @Override
  @Transactional
  public CourseListResponseDTO createCourse(
    CourseRequestDTO dto,
    String thumbnailUrl
  ) {
    Course course = new Course();
    course.setTitle(dto.getTitle());
    course.setDescription(dto.getDescription());
    course.setCourseType(dto.getCourseType());
    course.setPrice(dto.getPrice());
    course.setThumbnailUrl(thumbnailUrl);

    course.setDiscountPrice(dto.getDiscountPrice());
    course.setTotalLesson(0);
    course.setTotalDuration(0);
    courseRepository.save(course);

    return mapToListDTO(course);
  }

  @Override
  public void deleteCourse(Integer id) {
    if (!courseRepository.existsById(id)) {
      throw new RuntimeException("Course not found");
    }

    courseRepository.deleteById(id);
  }

  @Override
  public CourseListResponseDTO update(Integer id, CourseUpdateRequestDTO dto) {
    Course course = courseRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Course not found"));

    course.setTitle(dto.getTitle());
    course.setDescription(dto.getDescription());
    course.setCourseType(dto.getCourseType());
    course.setPrice(dto.getPrice());
    course.setThumbnailUrl(dto.getThumbnailUrl());
    course.setDiscountPrice(dto.getDiscountPrice());
    Course saved = courseRepository.save(course);
    return mapToListDTO(saved);
  }

  private CourseListResponseDTO mapToListDTO(Course course) {
    CourseListResponseDTO dto = new CourseListResponseDTO();
    dto.setCourseId(course.getCourseId());
    dto.setTitle(course.getTitle());
    dto.setThumbnailUrl(course.getThumbnailUrl());
    dto.setPrice(course.getPrice());
    dto.setDiscountPrice(course.getDiscountPrice());

    dto.setCourseType(course.getCourseType());
    dto.setDescription(course.getDescription());
    return dto;
  }

  @Override
  public CurriculumResponseDTO getCurriculum(Integer courseId) {
    Course course = courseRepository
      .findById(courseId)
      .orElseThrow(() -> new RuntimeException("Course not found"));

    List<Chapter> chapters =
      chapterRepository.findByCourseCourseIdOrderByOrderIndexAsc(courseId);

    List<ChapterCurriculumDTO> chapterDTOs = chapters
      .stream()
      .map(chapter -> {
        List<CourseContent> contents =
          courseContentRepository.findByChapterChapterIdOrderByOrderIndexAsc(
            chapter.getChapterId()
          );

        List<ContentCurriculumDTO> contentDTOs = contents
          .stream()
          .map(content -> {
            String title = null;

            if (
              "LESSON".equals(content.getContentType()) &&
              content.getLesson() != null
            ) {
              title = content.getLesson().getTitle();
            }

            if (
              "QUIZ".equals(content.getContentType()) &&
              content.getQuiz() != null
            ) {
              title = content.getQuiz().getTitle();
            }

            return ContentCurriculumDTO.builder()
              .contentType(content.getContentType())
              .orderIndex(content.getOrderIndex())
              .title(title)
              .build();
          })
          .toList();

        return ChapterCurriculumDTO.builder()
          .chapterId(chapter.getChapterId())
          .title(chapter.getTitle())
          .orderIndex(chapter.getOrderIndex())
          .contents(contentDTOs)
          .build();
      })
      .toList();

    return CurriculumResponseDTO.builder()
      .courseId(course.getCourseId())
      .title(course.getTitle())
      .chapters(chapterDTOs)
      .build();
  }

  private CourseContentResponseDTO mapContentToDTO(CourseContent content) {
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
              .videoUrl(
                Boolean.TRUE.equals(content.getLesson().getIsFree())
                  ? content.getLesson().getVideoUrl()
                  : null
              )
              .duration(content.getLesson().getDuration())
              .isFree(content.getLesson().getIsFree())
              .attachmentUrl(
                Boolean.TRUE.equals(content.getLesson().getIsFree())
                  ? content.getLesson().getAttachmentUrl()
                  : null
              )
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
