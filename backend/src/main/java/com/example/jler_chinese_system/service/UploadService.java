package com.example.jler_chinese_system.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
  String uploadFile(MultipartFile file);
  void deleteFile(String fileUrl);
}
