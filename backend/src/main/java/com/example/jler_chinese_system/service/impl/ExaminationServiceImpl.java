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
public class ExaminationServiceImpl implements ExaminationService {

  @PersistenceContext
  private EntityManager em;

  private final EntityLookup data;
  private final CurrentUser currentUser;
  private final LearningAccessService learningAccess;

  private void quizAccess(Quiz q) {
    User u = currentUser.get();
    if (
      "ADMIN".equals(u.getRole().getRoleName()) ||
      q.getCourseContents().isEmpty()
    ) return;
    check(
      q
        .getCourseContents()
        .stream()
        .anyMatch(x ->
          learningAccess.enrolled(u, x.getChapter().getCourse().getCourseId())
        ),
      "Bạn cần mua khóa học để làm bài thi này"
    );
  }

  public Map<String, Object> startQuiz(Integer id) {
    Quiz q = data.find(Quiz.class, id);
    quizAccess(q);
    check(Boolean.TRUE.equals(q.getActive()), "Đề thi đã được ẩn");
    check(!q.getQuizQuestions().isEmpty(), "Đề chưa có câu hỏi");
    User u = currentUser.get();
    em.lock(u, LockModeType.PESSIMISTIC_WRITE);
    var sessions = em
      .createQuery(
        "from QuizSession s where s.userId=:u and s.quizId=:q order by s.startedAt desc",
        QuizSession.class
      )
      .setParameter("u", u.getUserId())
      .setParameter("q", id)
      .getResultList();
    QuizSession s = sessions
      .stream()
      .filter(
        x -> !x.getSubmitted() && x.getExpiresAt().isAfter(LocalDateTime.now())
      )
      .findFirst()
      .orElse(null);
    if (s == null) {
      check(
        q.getMaxAttempts() == null ||
          q.getMaxAttempts() == 0 ||
          sessions.size() < q.getMaxAttempts(),
        "Bạn đã hết lượt làm bài"
      );
      s = new QuizSession();
      s.setUserId(u.getUserId());
      s.setQuizId(id);
      s.setStartedAt(LocalDateTime.now());
      s.setExpiresAt(LocalDateTime.now().plusMinutes(q.getTimeLimit()));
      data.save(s);
    }
    return map(
      "sessionId",
      s.getId(),
      "quizId",
      id,
      "title",
      q.getTitle(),
      "expiresAt",
      s.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant(),
      "questions",
      q
        .getQuizQuestions()
        .stream()
        .sorted(Comparator.comparing(QuizQuestion::getOrderIndex))
        .map(x -> questionView(x, false))
        .toList()
    );
  }

  public Map<String, Object> submitQuiz(
    Integer session,
    Map<String, Object> d
  ) {
    QuizSession s = data.find(QuizSession.class, session);
    em.lock(s, LockModeType.PESSIMISTIC_WRITE);
    check(
      s.getUserId().equals(currentUser.get().getUserId()),
      "Lượt thi không thuộc tài khoản này"
    );
    check(!s.getSubmitted(), "Bài thi đã được nộp");
    Quiz q = data.find(Quiz.class, s.getQuizId());
    quizAccess(q);
    check(
      !LocalDateTime.now().isAfter(s.getExpiresAt().plusSeconds(15)),
      "Bài thi đã quá hạn nộp"
    );
    Object raw = d.get("answers");
    check(raw instanceof Map, "Đáp án không hợp lệ");
    var answers = (Map<?, ?>) raw;
    QuizResult r = new QuizResult();
    r.setUser(currentUser.get());
    r.setQuiz(q);
    int correct = 0,
      idx = 0;
    for (var question : q
      .getQuizQuestions()
      .stream()
      .sorted(Comparator.comparing(QuizQuestion::getOrderIndex))
      .toList()) {
      Object selected = answers.get(question.getQuizQuestionId().toString());
      QuizOption option =
        selected == null
          ? null
          : question
              .getQuizOptions()
              .stream()
              .filter(o ->
                o.getQuizOptionId().toString().equals(selected.toString())
              )
              .findFirst()
              .orElseThrow(() ->
                new ResponseStatusException(
                  HttpStatus.BAD_REQUEST,
                  "Đáp án không thuộc câu hỏi"
                )
              );
      boolean ok = option != null && Boolean.TRUE.equals(option.getIsCorrect());
      if (ok) correct++;
      var a = new QuizAttemptDetail();
      a.setQuizResult(r);
      a.setQuizQuestion(question);
      a.setSelectedOption(option);
      a.setIsCorrect(ok);
      a.setOrderIndex(idx++);
      r.getAttemptDetails().add(a);
    }
    int n = q.getQuizQuestions().size();
    r.setTotalQuestions(n);
    r.setCorrectAnswers(correct);
    r.setWrongAnswers(n - correct);
    r.setScore((int) Math.round((correct * 100.0) / n));
    r.setStatus(r.getScore() >= q.getPassScore() ? "PASSED" : "FAILED");
    r.setTimeSpent(
      (int) Duration.between(s.getStartedAt(), LocalDateTime.now()).getSeconds()
    );
    data.save(r);
    s.setSubmitted(true);
    em.flush();
    return resultView(r);
  }

  public List<?> results() {
    return em
      .createQuery(
        "from QuizResult r where r.user.userId=:u order by r.submittedAt desc",
        QuizResult.class
      )
      .setParameter("u", currentUser.get().getUserId())
      .getResultList()
      .stream()
      .map(PlatformMapper::resultView)
      .toList();
  }

  public Map<String, Object> result(Integer id) {
    var r = data.find(QuizResult.class, id);
    check(
      r.getUser().getUserId().equals(currentUser.get().getUserId()) ||
        currentUser.isAdmin(),
      "Không có quyền xem kết quả"
    );
    return resultView(r);
  }
}
