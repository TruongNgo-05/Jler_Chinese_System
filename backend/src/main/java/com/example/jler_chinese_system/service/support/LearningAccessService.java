package com.example.jler_chinese_system.service.support;

import static com.example.jler_chinese_system.common.Checks.check;
import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.mapper.PlatformMapper.*;
import static com.example.jler_chinese_system.utils.RequestValues.*;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.mapper.PlatformMapper;
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
@Transactional(readOnly = true)
public class LearningAccessService {

  @PersistenceContext
  private EntityManager em;

  private final EntityLookup data;
  private final CurrentUser currentUser;

  public boolean enrolled(User u, Integer course) {
    return (
      em
        .createQuery(
          "select count(x) from UserCourse x where x.user.userId=:u and x.course.courseId=:c and x.status='ACTIVE'",
          Long.class
        )
        .setParameter("u", u.getUserId())
        .setParameter("c", course)
        .getSingleResult() > 0
    );
  }

  public void lessonAccess(Lesson l) {
    User u = currentUser.get();
    if ("ADMIN".equals(u.getRole().getRoleName())) return;
    boolean allowed = l
      .getCourseContents()
      .stream()
      .anyMatch(link -> {
        var chapter = link.getChapter();
        var course = chapter.getCourse();
        return (
          Boolean.TRUE.equals(chapter.getIsActive()) &&
          (enrolled(u, course.getCourseId()) ||
            (!"HIDDEN".equals(course.getStatus()) &&
              (Boolean.TRUE.equals(l.getIsFree()) ||
                Boolean.TRUE.equals(link.getIsPreview()))))
        );
      });
    check(allowed, "Bài học chưa công bố hoặc bạn chưa có quyền học khóa này");
  }
}
