package com.example.jler_chinese_system.service.impl;

import com.example.jler_chinese_system.service.*;
import java.nio.file.*;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UploadServiceImpl implements UploadService {

  @Value("${file.upload-dir}")
  private String directory;

  public String uploadFile(MultipartFile f) {
    if (
      f == null || f.isEmpty() || f.getSize() > 10 * 1024 * 1024
    ) throw new ResponseStatusException(
      HttpStatus.BAD_REQUEST,
      "Chọn ảnh tối đa 10MB"
    );
    try (
      var input = new javax.imageio.stream.MemoryCacheImageInputStream(
        f.getInputStream()
      )
    ) {
      var readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Chỉ nhận ảnh PNG hoặc JPEG"
      );
      var reader = readers.next();
      java.awt.image.BufferedImage image;
      try {
        reader.setInput(input);
        String format = reader.getFormatName();
        if (
          !format.equalsIgnoreCase("png") &&
          !format.equalsIgnoreCase("jpeg") &&
          !format.equalsIgnoreCase("jpg")
        ) throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Chỉ nhận ảnh PNG hoặc JPEG"
        );
        int w = reader.getWidth(0),
          h = reader.getHeight(0);
        if (
          w > 8000 || h > 8000 || (long) w * h > 32000000
        ) throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Ảnh tối đa 32 megapixel"
        );
        image = reader.read(0);
      } finally {
        reader.dispose();
      }
      var dir = Path.of(directory).toAbsolutePath().normalize();
      Files.createDirectories(dir);
      String name = UUID.randomUUID() + ".png";
      ImageIO.write(image, "png", dir.resolve(name).toFile());
      return "/uploads/" + name;
    } catch (java.io.IOException e) {
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Không lưu được ảnh"
      );
    }
  }

  public void deleteFile(String url) {
    if (url == null || !url.startsWith("/uploads/")) return;
    String name = url.substring(9);
    if (!name.matches("[a-zA-Z0-9_.-]+")) return;
    try {
      Files.deleteIfExists(
        Path.of(directory).toAbsolutePath().normalize().resolve(name)
      );
    } catch (java.io.IOException e) {
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Không xóa được ảnh"
      );
    }
  }
}
