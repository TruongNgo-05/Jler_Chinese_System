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
public class LearningServiceImpl implements LearningService {

  @PersistenceContext
  private EntityManager em;

  private final EntityLookup data;
  private final CurrentUser currentUser;
  private final LearningAccessService learningAccess;

  public Map<String, Object> learn(Integer id) {
    Course c = data.find(Course.class, id);
    check(
      currentUser.isAdmin() || learningAccess.enrolled(currentUser.get(), id),
      "Bạn chưa được cấp quyền học khóa này"
    );
    var m = courseView(c);
    m.put("chapters", chapters(c, true));
    m.put(
      "progress",
      em
        .createQuery(
          "from UserLessonProgress p where p.user.userId=:u",
          UserLessonProgress.class
        )
        .setParameter("u", currentUser.get().getUserId())
        .getResultList()
        .stream()
        .map(p ->
          map(
            "lessonId",
            p.getLesson().getLessonId(),
            "completed",
            p.getIsCompleted(),
            "lastPosition",
            p.getLastPosition()
          )
        )
        .toList()
    );
    return m;
  }

  public List<?> myCourses() {
    User u = currentUser.get();
    return em
      .createQuery(
        "from UserCourse x where x.user.userId=:u and x.status='ACTIVE'",
        UserCourse.class
      )
      .setParameter("u", u.getUserId())
      .getResultList()
      .stream()
      .map(x -> courseView(x.getCourse()))
      .toList();
  }

  public void progress(Integer id, Map<String, Object> d) {
    Lesson l = data.find(Lesson.class, id);
    learningAccess.lessonAccess(l);
    User u = currentUser.get();
    em.lock(u, LockModeType.PESSIMISTIC_WRITE);
    var list = em
      .createQuery(
        "from UserLessonProgress p where p.user=:u and p.lesson=:l",
        UserLessonProgress.class
      )
      .setParameter("u", u)
      .setParameter("l", l)
      .getResultList();
    var p = list.isEmpty() ? new UserLessonProgress() : list.get(0);
    p.setUser(u);
    p.setLesson(l);
    if (d.containsKey("lastPosition")) p.setLastPosition(
      Math.max(0, number(d, "lastPosition", 0))
    );
    if (bool(d, "completed", false)) {
      p.setIsCompleted(true);
      p.setCompletedAt(LocalDateTime.now());
    }
    if (list.isEmpty()) data.save(p);
  }

  public List<?> flashcards(Integer lesson) {
    Lesson l = data.find(Lesson.class, lesson);
    learningAccess.lessonAccess(l);
    return l.getFlashcards().stream().map(PlatformMapper::flashView).toList();
  }

  public List<?> comments(Integer lesson) {
    Lesson l = data.find(Lesson.class, lesson);
    learningAccess.lessonAccess(l);
    return em
      .createQuery(
        "from Comment c where c.lesson=:l order by c.createdAt desc",
        Comment.class
      )
      .setParameter("l", l)
      .getResultList()
      .stream()
      .map(c ->
        map(
          "id",
          c.getCommentId(),
          "content",
          c.getContent(),
          "user",
          c.getUser().getFullName(),
          "userId",
          c.getUser().getUserId(),
          "createdAt",
          c.getCreatedAt()
        )
      )
      .toList();
  }

  public void comment(Integer lesson, Map<String, Object> d) {
    Lesson l = data.find(Lesson.class, lesson);
    learningAccess.lessonAccess(l);
    var c = new Comment();
    c.setUser(currentUser.get());
    c.setLesson(l);
    c.setContent(required(d, "content"));
    data.save(c);
  }

  public void deleteComment(Integer id) {
    var c = data.find(Comment.class, id);
    check(
      c.getUser().getUserId().equals(currentUser.get().getUserId()) ||
        currentUser.isAdmin(),
      "Không thể xóa bình luận của người khác"
    );
    em.remove(c);
  }

  public List<?> words() {
    return em
      .createQuery(
        "from SavedWord w where w.userId=:u order by w.id desc",
        SavedWord.class
      )
      .setParameter("u", currentUser.get().getUserId())
      .getResultList()
      .stream()
      .map(w ->
        map(
          "id",
          w.getId(),
          "front",
          w.getFront(),
          "back",
          w.getBack(),
          "mastered",
          w.getMastered()
        )
      )
      .toList();
  }

  public void saveWord(Integer id, Map<String, Object> d) {
    var w = id == null ? new SavedWord() : data.find(SavedWord.class, id);
    if (id != null) check(
      w.getUserId().equals(currentUser.get().getUserId()),
      "Từ vựng không thuộc tài khoản này"
    );
    w.setUserId(currentUser.get().getUserId());
    w.setFront(required(d, "front"));
    w.setBack(required(d, "back"));
    check(
      w.getFront().length() <= 255 && w.getBack().length() <= 255,
      "Từ và nghĩa tối đa 255 ký tự"
    );
    w.setMastered(bool(d, "mastered", false));
    if (id == null) data.save(w);
  }

  public void deleteWord(Integer id) {
    var w = data.find(SavedWord.class, id);
    check(
      w.getUserId().equals(currentUser.get().getUserId()),
      "Từ vựng không thuộc tài khoản này"
    );
    em.remove(w);
  }

  public Map<String, Object> lesson(Integer id) {
    var l = data.find(Lesson.class, id);
    learningAccess.lessonAccess(l);
    return lessonView(l, true);
  }
}
