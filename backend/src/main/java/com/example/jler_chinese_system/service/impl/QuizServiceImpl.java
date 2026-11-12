package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.QuizCreateRequestDTO;
import com.example.jler_chinese_system.dto.QuizResponseDTO;
import com.example.jler_chinese_system.entity.Quiz;
import com.example.jler_chinese_system.repository.QuizRepository;
import com.example.jler_chinese_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizServiceImpl implements QuizService {

  private final QuizRepository quizRepository;

  @Override
  public QuizResponseDTO create(QuizCreateRequestDTO dto) {
    Quiz quiz = new Quiz();
    quiz.setTitle(dto.getTitle());
    quiz.setQuizType(dto.getQuizType());
    quiz.setPassScore(dto.getPassScore());
    quiz.setTimeLimit(dto.getTimeLimit());
    quiz.setMaxAttempts(dto.getMaxAttempts());
    quiz.setMaxPauses(dto.getMaxPauses());

    quizRepository.save(quiz);

    return mapToDTO(quiz);
  }

  @Override
  public QuizResponseDTO update(Integer id, QuizCreateRequestDTO dto) {
    Quiz quiz = quizRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Quiz not found"));

    quiz.setTitle(dto.getTitle());
    quiz.setQuizType(dto.getQuizType());
    quiz.setPassScore(dto.getPassScore());
    quiz.setTimeLimit(dto.getTimeLimit());
    quiz.setMaxAttempts(dto.getMaxAttempts());
    quiz.setMaxPauses(dto.getMaxPauses());

    return mapToDTO(quiz);
  }

  @Override
  public void delete(Integer id) {
    quizRepository.deleteById(id);
  }

  @Override
  public QuizResponseDTO getById(Integer id) {
    Quiz quiz = quizRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("Quiz not found"));

    return mapToDTO(quiz);
  }

  private QuizResponseDTO mapToDTO(Quiz quiz) {
    return QuizResponseDTO.builder()
      .quizId(quiz.getQuizId())
      .title(quiz.getTitle())
      .quizType(quiz.getQuizType())
      .passScore(quiz.getPassScore())
      .timeLimit(quiz.getTimeLimit())
      .maxAttempts(quiz.getMaxAttempts())
      .maxPauses(quiz.getMaxPauses())
      .build();
  }
}
