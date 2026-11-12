package com.example.jler_chinese_system.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumResponseDTO {

  private Integer courseId;
  private String title;
  private List<ChapterCurriculumDTO> chapters;
}
