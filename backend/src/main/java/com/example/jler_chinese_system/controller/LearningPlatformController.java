package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class LearningPlatformController {

  private final LearningService learningService;

  @GetMapping("/my-courses")
  public Object courses() {
    return learningService.myCourses();
  }

  @GetMapping("/learn/{id}")
  public Object learn(@PathVariable Integer id) {
    return learningService.learn(id);
  }

  @PutMapping("/lessons/{id}/progress")
  public void progress(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    learningService.progress(id, d);
  }

  @GetMapping("/lessons/{id}/flashcards")
  public Object cards(@PathVariable Integer id) {
    return learningService.flashcards(id);
  }

  @GetMapping("/lessons/{id}/comments")
  public Object comments(@PathVariable Integer id) {
    return learningService.comments(id);
  }

  @PostMapping("/lessons/{id}/comments")
  public void comment(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    learningService.comment(id, d);
  }

  @DeleteMapping("/comments/{id}")
  public void deleteComment(@PathVariable Integer id) {
    learningService.deleteComment(id);
  }

  @GetMapping("/lessons/{id}")
  public Object lesson(@PathVariable Integer id) {
    return learningService.lesson(id);
  }

  @GetMapping("/words")
  public Object words() {
    return learningService.words();
  }

  @PostMapping("/words")
  public void addWord(@RequestBody Map<String, Object> d) {
    learningService.saveWord(null, d);
  }

  @PutMapping("/words/{id}")
  public void word(
    @PathVariable Integer id,
    @RequestBody Map<String, Object> d
  ) {
    learningService.saveWord(id, d);
  }

  @DeleteMapping("/words/{id}")
  public void deleteWord(@PathVariable Integer id) {
    learningService.deleteWord(id);
  }
}
