package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class ExamController {

  private final ExaminationService examinationService;

  @PostMapping("/quizzes/{id}/start")
  public Object start(@PathVariable Integer id) {
    return examinationService.startQuiz(id);
  }

  @PostMapping("/quiz-sessions/{id}/submit")
  public Object submit(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    return examinationService.submitQuiz(id, d);
  }

  @GetMapping("/results")
  public Object results() {
    return examinationService.results();
  }

  @GetMapping("/results/{id}")
  public Object result(@PathVariable Integer id) {
    return examinationService.result(id);
  }
}
