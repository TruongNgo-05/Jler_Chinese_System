package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.CourseListResponseDTO;
import com.example.jler_chinese_system.dto.CourseRequestDTO;
import com.example.jler_chinese_system.dto.CourseUpdateRequestDTO;
import com.example.jler_chinese_system.dto.CurriculumResponseDTO;
import com.example.jler_chinese_system.service.CourseService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

  private final CourseService courseService;
  private final com.example.jler_chinese_system.service.UploadService uploadService;

  @GetMapping
  public ResponseEntity<List<CourseListResponseDTO>> getAllCourses() {
    return ResponseEntity.ok(courseService.getAllCourses(null));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createCourse(
    @RequestPart("course") CourseRequestDTO request,
    @RequestPart("thumbnail") MultipartFile thumbnail
  ) throws IOException {
    String thumbnailUrl = uploadService.uploadFile(thumbnail);

    // 3. Lưu vào DB
    courseService.createCourse(request, thumbnailUrl);

    return ResponseEntity.ok("Created");
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseListResponseDTO> updateCourse(
    @PathVariable Integer id,
    @Valid @RequestBody CourseUpdateRequestDTO dto
  ) {
    return ResponseEntity.ok(courseService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{courseId}/curriculum")
  public ResponseEntity<CurriculumResponseDTO> getCurriculum(
    @PathVariable Integer courseId
  ) {
    return ResponseEntity.ok(courseService.getCurriculum(courseId));
  }
}
