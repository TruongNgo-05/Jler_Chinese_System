package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.dto.CategoryDTO;
import com.example.jler_chinese_system.entity.Category;
import com.example.jler_chinese_system.repository.CategoryRepository;
import com.example.jler_chinese_system.service.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<CategoryDTO> getAllCategories() {
    return categoryRepository.findAll().stream().map(this::mapToDTO).toList();
  }

  private CategoryDTO mapToDTO(Category category) {
    return new CategoryDTO(
      category.getCategoryId(),
      category.getNameCategory()
    );
  }
}
