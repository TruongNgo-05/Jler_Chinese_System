package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.QuizCreateRequestDTO;
import com.example.jler_chinese_system.dto.QuizResponseDTO;
import com.example.jler_chinese_system.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
public class QuizController {

  private final QuizService quizService;

  // CREATE
  @PostMapping
  public QuizResponseDTO create(@RequestBody QuizCreateRequestDTO dto) {
    return quizService.create(dto);
  }

  // UPDATE
  @PutMapping("/{id}")
  public QuizResponseDTO update(
    @PathVariable Integer id,
    @RequestBody QuizCreateRequestDTO dto
  ) {
    return quizService.update(id, dto);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    quizService.delete(id);
  }

  // GET BY ID
  @GetMapping("/{id}")
  public QuizResponseDTO getById(@PathVariable Integer id) {
    return quizService.getById(id);
  }
}
