package com.example.jler_chinese_system.exception;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiErrors {

  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<?> status(ResponseStatusException e) {
    return ResponseEntity.status(e.getStatusCode()).body(
      Map.of(
        "message",
        e.getReason() == null ? "Yêu cầu không hợp lệ" : e.getReason()
      )
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> validation(MethodArgumentNotValidException e) {
    return ResponseEntity.badRequest().body(
      Map.of(
        "message",
        e
          .getBindingResult()
          .getFieldErrors()
          .stream()
          .map(x -> x.getField() + ": " + x.getDefaultMessage())
          .findFirst()
          .orElse("Dữ liệu không hợp lệ")
      )
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<?> conflict(
    Exception e,
    jakarta.servlet.http.HttpServletRequest request
  ) {
    if (
      request.getRequestURI().endsWith("/auth/register")
    ) return ResponseEntity.status(409).body(
      Map.of(
        "message",
        "Email hoặc tên đăng nhập đã tồn tại. Hãy kiểm tra và dùng thông tin khác."
      )
    );
    return ResponseEntity.status(409).body(
      Map.of(
        "message",
        "Dữ liệu đã tồn tại hoặc đang được sử dụng; hãy ẩn thay vì xóa."
      )
    );
  }

  @ExceptionHandler(
    org.springframework.security.access.AccessDeniedException.class
  )
  ResponseEntity<?> denied(Exception e) {
    return ResponseEntity.status(403).body(
      Map.of("message", "Bạn không có quyền thực hiện thao tác này")
    );
  }

  @ExceptionHandler(
    org.springframework.web.multipart.MaxUploadSizeExceededException.class
  )
  ResponseEntity<?> size(Exception e) {
    return ResponseEntity.status(413).body(
      Map.of("message", "Ảnh tối đa 10MB")
    );
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    java.util.NoSuchElementException.class,
  })
  ResponseEntity<?> invalid(Exception e) {
    return ResponseEntity.badRequest().body(
      Map.of("message", "Dữ liệu không hợp lệ")
    );
  }

  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<?> runtime(RuntimeException e) {
    org.slf4j.LoggerFactory.getLogger(ApiErrors.class).warn(
      "Request failed",
      e
    );
    return ResponseEntity.badRequest().body(
      Map.of("message", "Không thể xử lý yêu cầu. Kiểm tra dữ liệu và thử lại.")
    );
  }
}
