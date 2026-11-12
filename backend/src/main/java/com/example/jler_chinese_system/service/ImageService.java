package com.example.jler_chinese_system.service;

public interface ImageService {
  String deleteAllImages();

  String deleteImagesByBookId(Integer bookId);

  String deleteImageById(Integer imageId);
}
