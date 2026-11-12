package com.example.jler_chinese_system.controller;

import com.example.jler_chinese_system.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  // ================= DELETE ALL =================
  @DeleteMapping("/delete-all")
  public ResponseEntity<?> deleteAllImages() {
    return ResponseEntity.ok(imageService.deleteAllImages());
  }

  // ================= DELETE BY BOOK =================
  @DeleteMapping("/delete-by-book/{bookId}")
  public ResponseEntity<?> deleteImagesByBookId(@PathVariable Integer bookId) {
    return ResponseEntity.ok(imageService.deleteImagesByBookId(bookId));
  }

  // ================= DELETE BY IMAGE =================
  @DeleteMapping("/{imageId}")
  public ResponseEntity<?> deleteImageById(@PathVariable Integer imageId) {
    return ResponseEntity.ok(imageService.deleteImageById(imageId));
  }
}
