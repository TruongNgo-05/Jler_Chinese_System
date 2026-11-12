package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.QuizCreateRequestDTO;
import com.example.jler_chinese_system.dto.QuizResponseDTO;
import com.example.jler_chinese_system.entity.Quiz;

public interface QuizService {
  QuizResponseDTO create(QuizCreateRequestDTO dto);

  QuizResponseDTO update(Integer id, QuizCreateRequestDTO dto);

  void delete(Integer id);

  QuizResponseDTO getById(Integer id);
}
