package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.CourseDetailResponseDTO;
import com.example.jler_chinese_system.dto.CourseListResponseDTO;
import com.example.jler_chinese_system.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class PublicCourseController {

  private final CourseService courseService;

  @GetMapping
  public ResponseEntity<List<CourseListResponseDTO>> getAllCourses(
    @RequestParam(required = false) String type
  ) {
    return ResponseEntity.ok(courseService.getAllCourses(type));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseDetailResponseDTO> getCourseDetail(
    @PathVariable Integer id
  ) {
    return ResponseEntity.ok(courseService.getCourseDetail(id));
  }
}
