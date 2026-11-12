package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.LessonCreateRequestDTO;
import com.example.jler_chinese_system.dto.LessonResponseDTO;
import com.example.jler_chinese_system.entity.Lesson;

public interface LessonService {
  LessonResponseDTO create(LessonCreateRequestDTO dto);

  LessonResponseDTO update(Integer id, LessonCreateRequestDTO dto);

  void delete(Integer id);

  LessonResponseDTO getById(Integer id);
}
