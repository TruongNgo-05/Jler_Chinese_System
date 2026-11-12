package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUploadController {

  private final UploadService uploadService;

  @PostMapping("/upload")
  public ResponseEntity<String> upload(
    @RequestParam("file") MultipartFile file
  ) {
    String imageUrl = uploadService.uploadFile(file);

    return ResponseEntity.ok(imageUrl);
  }
}
