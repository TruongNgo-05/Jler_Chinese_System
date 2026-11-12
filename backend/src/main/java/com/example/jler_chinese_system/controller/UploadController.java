package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

  private final UploadService uploadService;

  public UploadController(UploadService uploadService) {
    this.uploadService = uploadService;
  }

  @PostMapping
  public ResponseEntity<String> uploadImage(
    @RequestParam("file") MultipartFile file
  ) {
    String fileUrl = uploadService.uploadFile(file);

    return ResponseEntity.ok(fileUrl);
  }
}
