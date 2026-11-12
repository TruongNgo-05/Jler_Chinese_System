package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
public class CatalogController {

  private final CatalogService catalogService;

  @GetMapping("/catalog/{kind}")
  public Object catalog(@PathVariable String kind) {
    return catalogService.catalog(kind);
  }

  @GetMapping("/catalog/{kind}/{id}")
  public Object detail(@PathVariable String kind, @PathVariable Integer id) {
    return catalogService.detail(kind, id);
  }
}
