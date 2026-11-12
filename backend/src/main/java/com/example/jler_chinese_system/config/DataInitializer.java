package com.example.jler_chinese_system.config;

import com.example.jler_chinese_system.entity.*;
import com.example.jler_chinese_system.repository.*;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
  name = "app.initialize",
  havingValue = "true",
  matchIfMissing = true
)
public class DataInitializer implements CommandLineRunner {

  private final EntityManager em;
  private final RoleRepository roles;
  private final UserRepository users;
  private final PasswordEncoder encoder;

  @Value("${app.seed-demo:false}")
  private boolean demo;

  @Value("${app.admin-password:}")
  private String adminPassword;

  @Override
  @Transactional
  public void run(String... args) {
    // Canonical roles only; preserve admins and migrate every other legacy role to USER.
    var existingRoles = roles.findAll();
    Role adminRole = existingRoles
      .stream()
      .filter(r -> "ADMIN".equalsIgnoreCase(r.getRoleName()))
      .findFirst()
      .orElse(null);
    Role userRole = existingRoles
      .stream()
      .filter(r -> "USER".equalsIgnoreCase(r.getRoleName()))
      .findFirst()
      .orElse(null);
    if (adminRole == null) {
      adminRole = new Role();
      adminRole.setRoleName("ADMIN");
      roles.save(adminRole);
    }
    if (userRole == null) {
      userRole = new Role();
      userRole.setRoleName("USER");
      roles.save(userRole);
    }
    adminRole.setRoleName("ADMIN");
    userRole.setRoleName("USER");
    for (Role old : existingRoles) {
      if (
        old.getRoleId().equals(adminRole.getRoleId()) ||
        old.getRoleId().equals(userRole.getRoleId())
      ) continue;
      Role replacement = "ADMIN".equalsIgnoreCase(old.getRoleName())
        ? adminRole
        : userRole;
      em.createQuery("delete from RefreshToken t where t.user.role=:r")
        .setParameter("r", old)
        .executeUpdate();
      em.createQuery(
        "update User u set u.role=:replacement,u.tokenVersion=coalesce(u.tokenVersion,0)+1 where u.role=:old"
      )
        .setParameter("replacement", replacement)
        .setParameter("old", old)
        .executeUpdate();
      em.remove(old);
    }
    em.flush();
    if (users.findByUserName("admin").isEmpty() && !adminPassword.isBlank()) {
      if (adminPassword.length() < 12) throw new IllegalArgumentException(
        "ADMIN_PASSWORD cần ít nhất 12 ký tự"
      );
      User u = new User();
      u.setUserName("admin");
      u.setEmail("admin@jler.local");
      u.setFullName("Quản trị JLer");
      u.setPassWord(encoder.encode(adminPassword));
      u.setRole(roles.findByRoleName("ADMIN").orElseThrow());
      u.setStatus(true);
      u.setCreatedAt(LocalDateTime.now());
      users.save(u);
    }
    if (
      !demo ||
      em
        .createQuery("select count(b) from Book b", Long.class)
        .getSingleResult() > 0
    ) return;
    List<Category> cats = new ArrayList<>();
    for (String title : List.of("Giáo trình HSK", "Giao tiếp", "Luyện thi")) {
      Category c = new Category();
      c.setNameCategory(title);
      em.persist(c);
      cats.add(c);
    }
    String[] names = {
      "Chinh phục HSK 1",
      "Ngữ pháp tiếng Trung cơ bản",
      "Từ vựng HSK 2 qua hình ảnh",
      "Giao tiếp mỗi ngày",
      "Bộ đề luyện thi HSK 3",
      "Tập viết Hán tự",
    };
    for (int i = 0; i < names.length; i++) {
      Book b = new Book();
      b.setTitle(names[i]);
      b.setDescription(
        "Tài liệu JLer: " +
          names[i] +
          ". Nội dung giới thiệu phục vụ kiểm thử cửa hàng; quản trị viên thay bằng sách và thông tin xuất bản thực tế trước khi bán."
      );
      b.setPrice(BigDecimal.valueOf(120000 + i * 20000));
      b.setStock(20 + i);
      b.getCategories().add(cats.get(i % 3));
      BookImage image = new BookImage();
      image.setBook(b);
      image.setImageUrl(
        "/uploads/jler-media/" +
          List.of("study-desk.png", "hsk-books.png", "conversation.png").get(
            i % 3
          )
      );
      image.setIsThumbNail(true);
      b.getBookImages().add(image);
      em.persist(b);
    }
    for (int i = 1; i <= 3; i++) {
      Course c = new Course();
      c.setTitle(
        i == 1
          ? "Tiếng Trung từ con số 0"
          : i == 2
            ? "HSK 2 – Vững nền tảng"
            : "Tiếng Trung giao tiếp thực tế"
      );
      c.setDescription(
        "Khóa học giúp bạn làm quen phát âm, từ vựng và câu giao tiếp. Bài học văn bản được cung cấp; video và tài liệu do quản trị viên bổ sung."
      );
      c.setPrice(BigDecimal.valueOf(i * 199000));
      c.setCourseType(i < 3 ? "HSK" : "Giao tiếp");
      c.setStatus("ACTIVE");
      c.setThumbnailUrl(
        "/uploads/jler-media/" +
          List.of("study-desk.png", "hsk-books.png", "conversation.png").get(
            i - 1
          )
      );
      em.persist(c);
      Chapter ch = new Chapter();
      ch.setCourse(c);
      ch.setTitle("Bắt đầu hành trình");
      ch.setOrderIndex(1);
      em.persist(ch);
      for (int n = 1; n <= 2; n++) {
        Lesson l = new Lesson();
        l.setTitle(n == 1 ? "Chào hỏi và giới thiệu" : "Đếm số và hỏi giá");
        l.setContent(
          n == 1
            ? "你好！Nǐ hǎo! — Xin chào.\n我叫安。Wǒ jiào Ān. — Tôi tên là An.\n谢谢！Xièxie! — Cảm ơn.\n再见！Zàijiàn! — Tạm biệt.\n\nThực hành: đọc to từng câu, sau đó tự giới thiệu tên của bạn."
            : "一 yī — một; 二 èr — hai; 三 sān — ba.\n多少钱？Duōshao qián? — Bao nhiêu tiền?\n十块钱。Shí kuài qián. — Mười tệ.\n\nThực hành: ghép số và hỏi giá một đồ vật quanh bạn."
        );
        l.setVideoUrl(
          n == 1
            ? "/uploads/jler-media/chao-hoi.mp4"
            : "/uploads/jler-media/so-dem.mp4"
        );
        l.setDuration(n == 1 ? 42 : 72);
        l.setIsFree(n == 1);
        em.persist(l);
        CourseContent link = new CourseContent();
        link.setChapter(ch);
        link.setLesson(l);
        link.setContentType("LESSON");
        link.setOrderIndex(n);
        link.setIsPreview(n == 1);
        em.persist(link);
        for (String[] pair : new String[][] {
          { "你好 · nǐ hǎo", "Xin chào" },
          { "谢谢 · xièxie", "Cảm ơn" },
          { "再见 · zàijiàn", "Tạm biệt" },
        }) {
          Flashcard f = new Flashcard();
          f.setLesson(l);
          f.setFronttext(pair[0]);
          f.setBacktext(pair[1]);
          em.persist(f);
        }
      }
    }
    Quiz q = new Quiz();
    q.setTitle("Làm quen tiếng Trung – Chào hỏi");
    q.setQuizType("PRACTICE");
    q.setTimeLimit(10);
    q.setPassScore(60);
    q.setMaxAttempts(0);
    q.setMaxPauses(0);
    String[][] qs = {
      { "你好 có nghĩa là gì?", "Xin chào", "Cảm ơn", "Tạm biệt" },
      { "谢谢 được đọc như thế nào?", "Xièxie", "Nǐ hǎo", "Zàijiàn" },
      { "Tạm biệt trong tiếng Trung là gì?", "再见", "你好", "谢谢" },
    };
    for (int i = 0; i < qs.length; i++) {
      QuizQuestion x = new QuizQuestion();
      x.setQuiz(q);
      x.setContent(qs[i][0]);
      x.setQuestionType("SINGLE_CHOICE");
      x.setOrderIndex(i);
      for (int n = 1; n <= 3; n++) {
        QuizOption o = new QuizOption();
        o.setQuizquestion(x);
        o.setContent(qs[i][n]);
        o.setIsCorrect(n == 1);
        x.getQuizOptions().add(o);
      }
      q.getQuizQuestions().add(x);
    }
    em.persist(q);
    PlatformPost p = new PlatformPost();
    p.setTitle("Một hành trình mới, một ngôn ngữ mới");
    p.setType("BANNER");
    p.setContent(
      "Học tiếng Trung theo cách của bạn. Khám phá khóa học, giáo trình và luyện tập mỗi ngày cùng JLer."
    );
    p.setImageUrl("/uploads/jler-media/study-desk.png");
    em.persist(p);
    PlatformPost blog = new PlatformPost();
    blog.setTitle("Bắt đầu học tiếng Trung từ đâu?");
    blog.setType("BLOG");
    blog.setContent(
      "Hãy bắt đầu với phiên âm Pinyin và bốn thanh điệu. Mỗi ngày dành một khoảng thời gian ngắn để nghe, đọc to và ôn lại từ đã học.\n\nĐặt mục tiêu vừa sức: giới thiệu bản thân, chào hỏi và gọi món. Khi đã quen các câu ngắn, bạn có thể học theo một giáo trình có lộ trình rõ ràng."
    );
    em.persist(blog);
    Voucher v = new Voucher();
    v.setCode("JLER10");
    v.setType("PERCENT");
    v.setDiscount(BigDecimal.TEN);
    v.setMinOrder(BigDecimal.valueOf(100000));
    v.setMaxDiscount(BigDecimal.valueOf(50000));
    v.setExpiredAt(LocalDate.now().plusYears(1));
    em.persist(v);
  }
}
