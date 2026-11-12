package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.dto.CategoryDTO;
import com.example.jler_chinese_system.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<CategoryDTO> getAll() {
    return categoryService.getAllCategories();
  }
}
