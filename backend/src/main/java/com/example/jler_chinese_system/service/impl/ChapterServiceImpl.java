package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.ChapterCreateRequestDTO;
import com.example.jler_chinese_system.dto.ChapterResponseDTO;
import com.example.jler_chinese_system.entity.Chapter;
import com.example.jler_chinese_system.entity.Course;
import com.example.jler_chinese_system.repository.ChapterRepository;
import com.example.jler_chinese_system.repository.CourseRepository;
import com.example.jler_chinese_system.service.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

  private final ChapterRepository chapterRepository;
  private final CourseRepository courseRepository;

  @Override
  public ChapterResponseDTO create(
    Integer courseId,
    ChapterCreateRequestDTO dto
  ) {
    Course course = courseRepository
      .findById(courseId)
      .orElseThrow(() -> new RuntimeException("Course not found"));

    Integer maxOrder = chapterRepository.findMaxOrderIndex(courseId);

    Chapter chapter = new Chapter();
    chapter.setTitle(dto.getTitle());
    chapter.setCourse(course);
    chapter.setOrderIndex(maxOrder + 1);
    chapter.setIsActive(true);

    chapterRepository.save(chapter);

    return mapToDTO(chapter);
  }

  @Override
  public ChapterResponseDTO update(
    Integer chapterId,
    ChapterCreateRequestDTO dto
  ) {
    Chapter chapter = chapterRepository
      .findById(chapterId)
      .orElseThrow(() -> new RuntimeException("Chapter not found"));

    chapter.setTitle(dto.getTitle());

    return mapToDTO(chapter);
  }

  @Override
  public void delete(Integer chapterId) {
    chapterRepository.deleteById(chapterId);
  }

  @Override
  public List<ChapterResponseDTO> getByCourse(Integer courseId) {
    return chapterRepository
      .findByCourseCourseIdOrderByOrderIndexAsc(courseId)
      .stream()
      .map(this::mapToDTO)
      .toList();
  }

  private ChapterResponseDTO mapToDTO(Chapter chapter) {
    return ChapterResponseDTO.builder()
      .chapterId(chapter.getChapterId())
      .title(chapter.getTitle())
      .orderIndex(chapter.getOrderIndex())
      .isActive(chapter.getIsActive())
      .build();
  }
}
