package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.LessonCreateRequestDTO;
import com.example.jler_chinese_system.dto.LessonResponseDTO;
import com.example.jler_chinese_system.service.LessonService;
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
@RequestMapping("/api/admin/lessons")
@RequiredArgsConstructor
public class LessonController {

  private final LessonService lessonService;

  // CREATE
  @PostMapping
  public LessonResponseDTO create(@RequestBody LessonCreateRequestDTO dto) {
    return lessonService.create(dto);
  }

  // UPDATE
  @PutMapping("/{id}")
  public LessonResponseDTO update(
    @PathVariable Integer id,
    @RequestBody LessonCreateRequestDTO dto
  ) {
    return lessonService.update(id, dto);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    lessonService.delete(id);
  }

  // GET BY ID
  @GetMapping("/{id}")
  public LessonResponseDTO getById(@PathVariable Integer id) {
    return lessonService.getById(id);
  }
}
