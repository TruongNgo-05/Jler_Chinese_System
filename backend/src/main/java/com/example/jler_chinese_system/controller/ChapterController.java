package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.ChapterCreateRequestDTO;
import com.example.jler_chinese_system.dto.ChapterResponseDTO;
import com.example.jler_chinese_system.service.ChapterService;
import java.util.List;
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
@RequestMapping("/api/admin/chapters")
@RequiredArgsConstructor
public class ChapterController {

  private final ChapterService chapterService;

  @PostMapping("/course/{courseId}")
  public ChapterResponseDTO create(
    @PathVariable Integer courseId,
    @RequestBody ChapterCreateRequestDTO dto
  ) {
    return (ChapterResponseDTO) chapterService.create(courseId, dto);
  }

  @GetMapping("/course/{courseId}")
  public List<ChapterResponseDTO> getByCourse(@PathVariable Integer courseId) {
    return chapterService.getByCourse(courseId);
  }

  @PutMapping("/{chapterId}")
  public ChapterResponseDTO update(
    @PathVariable Integer chapterId,
    @RequestBody ChapterCreateRequestDTO dto
  ) {
    return chapterService.update(chapterId, dto);
  }

  @DeleteMapping("/{chapterId}")
  public void delete(@PathVariable Integer chapterId) {
    chapterService.delete(chapterId);
  }
}
