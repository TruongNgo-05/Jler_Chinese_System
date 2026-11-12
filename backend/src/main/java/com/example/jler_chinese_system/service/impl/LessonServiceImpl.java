package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.LessonCreateRequestDTO;
import com.example.jler_chinese_system.dto.LessonResponseDTO;
import com.example.jler_chinese_system.entity.Lesson;
import com.example.jler_chinese_system.repository.LessonRepository;
import com.example.jler_chinese_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {

  private final LessonRepository lessonRepository;

  @Override
  public LessonResponseDTO create(LessonCreateRequestDTO dto) {
    Lesson lesson = new Lesson();
    lesson.setTitle(dto.getTitle());
    lesson.setVideoUrl(dto.getVideoUrl());
    lesson.setDuration(dto.getDuration());
    lesson.setIsFree(dto.getIsFree());
    lesson.setAttachmentUrl(dto.getAttachmentUrl());

    lessonRepository.save(lesson);

    return mapToDTO(lesson);
  }

  @Override
  public LessonResponseDTO update(Integer id, LessonCreateRequestDTO dto) {
    Lesson lesson = lessonRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Lesson not found"));

    lesson.setTitle(dto.getTitle());
    lesson.setVideoUrl(dto.getVideoUrl());
    lesson.setDuration(dto.getDuration());
    lesson.setIsFree(dto.getIsFree());
    lesson.setAttachmentUrl(dto.getAttachmentUrl());

    return mapToDTO(lesson);
  }

  @Override
  public void delete(Integer id) {
    lessonRepository.deleteById(id);
  }

  @Override
  public LessonResponseDTO getById(Integer id) {
    Lesson lesson = lessonRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Lesson not found"));

    return mapToDTO(lesson);
  }

  private LessonResponseDTO mapToDTO(Lesson lesson) {
    return LessonResponseDTO.builder()
      .lessonId(lesson.getLessonId())
      .title(lesson.getTitle())
      .videoUrl(lesson.getVideoUrl())
      .duration(lesson.getDuration())
      .isFree(lesson.getIsFree())
      .attachmentUrl(lesson.getAttachmentUrl())
      .build();
  }
}
