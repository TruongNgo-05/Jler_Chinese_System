package com.example.jler_chinese_system.service.impl;

import static com.example.jler_chinese_system.common.Checks.check;
import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.mapper.PlatformMapper.*;
import static com.example.jler_chinese_system.utils.RequestValues.*;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.mapper.PlatformMapper;
import com.example.jler_chinese_system.repository.UserRepository;
import com.example.jler_chinese_system.security.CurrentUser;
import com.example.jler_chinese_system.service.*;
import com.example.jler_chinese_system.service.support.*;
import jakarta.persistence.*;
import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogServiceImpl implements CatalogService {

  private final EntityLookup data;

  public List<Map<String, Object>> catalog(String kind) {
    return switch (kind) {
      case "books" -> data
        .all(Book.class)
        .stream()
        .map(PlatformMapper::bookView)
        .toList();
      case "courses" -> data
        .all(Course.class)
        .stream()
        .filter(c -> !"HIDDEN".equals(c.getStatus()))
        .map(PlatformMapper::courseView)
        .toList();
      case "categories" -> data
        .all(Category.class)
        .stream()
        .map(c -> map("id", c.getCategoryId(), "name", c.getNameCategory()))
        .toList();
      case "posts" -> data
        .all(PlatformPost.class)
        .stream()
        .filter(p -> Boolean.TRUE.equals(p.getActive()))
        .map(PlatformMapper::postView)
        .toList();
      case "quizzes" -> data
        .all(Quiz.class)
        .stream()
        .filter(
          q ->
            Boolean.TRUE.equals(q.getActive()) &&
            q.getCourseContents().isEmpty()
        )
        .map(q -> quizView(q, false))
        .toList();
      default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    };
  }

  public Map<String, Object> detail(String kind, Integer id) {
    if (kind.equals("books")) return bookView(data.find(Book.class, id));
    if (kind.equals("courses")) {
      Course c = data.find(Course.class, id);
      check(!"HIDDEN".equals(c.getStatus()), "Khóa học đã ẩn");
      var m = courseView(c);
      m.put("chapters", chapters(c, false));
      return m;
    }
    if (kind.equals("posts")) {
      var p = data.find(PlatformPost.class, id);
      check(Boolean.TRUE.equals(p.getActive()), "Bài viết đã ẩn");
      return postView(p);
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }
}
