package com.example.jler_chinese_system.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ChapterResponseDTO {

  private Integer chapterId;
  private String title;
  private Integer orderIndex;
  private List<CourseContentResponseDTO> contents;
  private Boolean isActive;
}
