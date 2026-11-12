package com.example.jler_chinese_system.service;

import com.example.jler_chinese_system.dto.ChapterCreateRequestDTO;
import com.example.jler_chinese_system.dto.ChapterResponseDTO;
import com.example.jler_chinese_system.entity.Chapter;
import java.util.List;

public interface ChapterService {
  ChapterResponseDTO create(Integer courseId, ChapterCreateRequestDTO dto);

  void delete(Integer chapterId);

  List<ChapterResponseDTO> getByCourse(Integer courseId);

  ChapterResponseDTO update(Integer chapterId, ChapterCreateRequestDTO dto);
}
