package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.repository.BookImageRepository;
import com.example.jler_chinese_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

  private final BookImageRepository repository;

  // Legacy endpoints detach book images only. Files may also be used by profiles,
  // banners or other products; never delete every file in the upload directory.
  public String deleteAllImages() {
    repository.deleteAll();
    return "Đã gỡ ảnh khỏi danh sách sách";
  }

  public String deleteImagesByBookId(Integer bookId) {
    repository.deleteAll(repository.findByBook_BookId(bookId));
    return "Đã gỡ ảnh sách";
  }

  public String deleteImageById(Integer imageId) {
    repository.deleteById(imageId);
    return "Đã gỡ ảnh sách";
  }
}
