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
public class AdministrationServiceImpl implements AdministrationService {

  @PersistenceContext
  private EntityManager em;

  private final EntityLookup data;
  private final CurrentUser currentUser;
  private final UserRepository users;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;
  private final SePayService sepay;
  private final CatalogService catalogService;
  private final ShoppingService shoppingService;
  private final ReportService reportService;

  public List<?> adminList(String kind) {
    return switch (kind) {
      case "books" -> data
        .all(Book.class)
        .stream()
        .map(PlatformMapper::bookView)
        .toList();
      case "courses" -> data
        .all(Course.class)
        .stream()
        .map(PlatformMapper::courseView)
        .toList();
      case "categories" -> catalogService.catalog("categories");
      case "users" -> data
        .all(User.class)
        .stream()
        .map(PlatformMapper::userView)
        .toList();
      case "orders" -> shoppingService.orders(true);
      case "reports" -> List.of(reportService.reports(null, null));
      case "sepay-transactions" -> sepay.transactions();
      case "posts" -> data
        .all(PlatformPost.class)
        .stream()
        .map(PlatformMapper::postView)
        .toList();
      case "vouchers" -> data
        .all(Voucher.class)
        .stream()
        .map(PlatformMapper::voucherView)
        .toList();
      case "quizzes" -> data
        .all(Quiz.class)
        .stream()
        .map(q -> quizView(q, true))
        .toList();
      case "chapters" -> data
        .all(Chapter.class)
        .stream()
        .map(c ->
          map(
            "id",
            c.getChapterId(),
            "title",
            c.getTitle(),
            "courseId",
            c.getCourse().getCourseId(),
            "orderIndex",
            c.getOrderIndex(),
            "active",
            c.getIsActive()
          )
        )
        .toList();
      case "lessons" -> data
        .all(Lesson.class)
        .stream()
        .map(l -> {
          var m = lessonView(l, true);
          m.put(
            "chapterId",
            l
              .getCourseContents()
              .stream()
              .findFirst()
              .map(c -> c.getChapter().getChapterId())
              .orElse(null)
          );
          m.put(
            "orderIndex",
            l
              .getCourseContents()
              .stream()
              .findFirst()
              .map(CourseContent::getOrderIndex)
              .orElse(0)
          );
          return m;
        })
        .toList();
      case "flashcards" -> data
        .all(Flashcard.class)
        .stream()
        .map(PlatformMapper::flashView)
        .toList();
      default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    };
  }

  public Map<String, Object> dashboard() {
    BigDecimal revenue = data
      .all(Order.class)
      .stream()
      .filter(o -> o.getPaymentStatus() == Order.PaymentStatus.paid)
      .map(Order::getTotalAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    return map(
      "users",
      users.count(),
      "books",
      data.all(Book.class).size(),
      "courses",
      data.all(Course.class).size(),
      "orders",
      data.all(Order.class).size(),
      "pendingOrders",
      data
        .all(Order.class)
        .stream()
        .filter(o -> "PENDING".equals(o.getStatus()))
        .count(),
      "revenue",
      revenue,
      "lowStock",
      data
        .all(Book.class)
        .stream()
        .filter(b -> b.getStock() < 5)
        .map(PlatformMapper::bookView)
        .toList()
    );
  }

  private void setPrices(
    Map<String, Object> d,
    java.util.function.Consumer<BigDecimal> regular,
    java.util.function.Consumer<BigDecimal> sale
  ) {
    var p = money(d, "price");
    regular.accept(p);
    BigDecimal discount =
      d.get("discountPrice") == null || text(d, "discountPrice").isBlank()
        ? null
        : money(d, "discountPrice");
    check(
      discount == null || discount.compareTo(p) <= 0,
      "Giá giảm không được lớn hơn giá gốc"
    );
    sale.accept(discount);
  }

  public Map<String, Object> adminSave(
    String kind,
    Integer id,
    Map<String, Object> d
  ) {
    switch (kind) {
      case "categories": {
        Category c =
          id == null ? new Category() : data.find(Category.class, id);
        c.setNameCategory(required(d, "name"));
        if (id == null) data.save(c);
        return map("id", c.getCategoryId());
      }
      case "books": {
        Book b = id == null ? new Book() : data.find(Book.class, id);
        if (id != null) em.lock(b, LockModeType.PESSIMISTIC_WRITE);
        b.setTitle(required(d, "title"));
        b.setDescription(required(d, "description"));
        setPrices(d, b::setPrice, b::setDiscountPrice);
        int stock = number(d, "stock", 0);
        check(stock >= 0, "Tồn kho không được âm");
        b.setStock(stock);
        b.getCategories().clear();
        Object ids = d.getOrDefault("categoryIds", List.of());
        check(ids instanceof List, "Danh mục không hợp lệ");
        for (Object x : (List<?>) ids)
          b.getCategories().add(
            data.find(Category.class, Integer.valueOf(x.toString()))
          );
        String image = url(d, "imageUrl");
        List<String> images = new ArrayList<>();
        if (!image.isBlank()) images.add(image);
        if (d.get("images") instanceof List<?> list) for (Object x : list) {
          String u = x.toString();
          url(map("imageUrl", u), "imageUrl");
          if (!u.isBlank() && !images.contains(u)) images.add(u);
        }
        b.getBookImages().clear();
        for (String u : images) {
          BookImage img = new BookImage();
          img.setBook(b);
          img.setImageUrl(u);
          img.setIsThumbNail(b.getBookImages().isEmpty());
          b.getBookImages().add(img);
        }
        if (id == null) data.save(b);
        em.flush();
        return bookView(b);
      }
      case "courses": {
        Course c = id == null ? new Course() : data.find(Course.class, id);
        c.setTitle(required(d, "title"));
        c.setDescription(required(d, "description"));
        setPrices(d, c::setPrice, c::setDiscountPrice);
        c.setCourseType(required(d, "courseType"));
        c.setThumbnailUrl(url(d, "imageUrl"));
        c.setStatus(bool(d, "active", true) ? "ACTIVE" : "HIDDEN");
        if (id == null) data.save(c);
        return courseView(c);
      }
      case "chapters": {
        Chapter c = id == null ? new Chapter() : data.find(Chapter.class, id);
        c.setTitle(required(d, "title"));
        c.setCourse(data.find(Course.class, number(d, "courseId", 0)));
        c.setOrderIndex(number(d, "orderIndex", 0));
        c.setIsActive(bool(d, "active", true));
        if (id == null) data.save(c);
        return map("id", c.getChapterId());
      }
      case "lessons": {
        Lesson l = id == null ? new Lesson() : data.find(Lesson.class, id);
        l.setTitle(required(d, "title"));
        l.setContent(text(d, "content"));
        l.setVideoUrl(url(d, "videoUrl"));
        l.setAttachmentUrl(url(d, "attachmentUrl"));
        l.setDuration(number(d, "duration", 0));
        check(l.getDuration() >= 0, "Thời lượng không hợp lệ");
        l.setIsFree(bool(d, "isFree", false));
        if (id == null) data.save(l);
        Chapter ch = data.find(Chapter.class, number(d, "chapterId", 0));
        var links = em
          .createQuery(
            "from CourseContent x where x.lesson=:l",
            CourseContent.class
          )
          .setParameter("l", l)
          .getResultList();
        CourseContent link = links.isEmpty()
          ? new CourseContent()
          : links.get(0);
        link.setChapter(ch);
        link.setLesson(l);
        link.setContentType("LESSON");
        link.setIsPreview(l.getIsFree());
        link.setOrderIndex(number(d, "orderIndex", 0));
        if (links.isEmpty()) data.save(link);
        return lessonView(l, true);
      }
      case "flashcards": {
        Flashcard f =
          id == null ? new Flashcard() : data.find(Flashcard.class, id);
        f.setLesson(data.find(Lesson.class, number(d, "lessonId", 0)));
        f.setFronttext(required(d, "front"));
        f.setBacktext(required(d, "back"));
        f.setImageUrl(url(d, "imageUrl"));
        if (id == null) data.save(f);
        return flashView(f);
      }
      case "quizzes": {
        Quiz q = id == null ? new Quiz() : data.find(Quiz.class, id);
        if (id != null) {
          em.lock(q, LockModeType.PESSIMISTIC_WRITE);
          check(
            q.getQuizResults().isEmpty() &&
              em
                .createQuery(
                  "select count(s) from QuizSession s where s.quizId=:q",
                  Long.class
                )
                .setParameter("q", id)
                .getSingleResult() == 0,
            "Đề đã có lượt thi. Hãy tạo đề mới để giữ lịch sử"
          );
        }
        q.setTitle(required(d, "title"));
        q.setActive(bool(d, "active", true));
        q.setQuizType(text(d, "quizType"));
        q.setPassScore(number(d, "passScore", 60));
        q.setTimeLimit(number(d, "timeLimit", 30));
        q.setMaxAttempts(number(d, "maxAttempts", 0));
        q.setMaxPauses(0);
        check(
          q.getPassScore() >= 0 &&
            q.getPassScore() <= 100 &&
            q.getTimeLimit() > 0 &&
            q.getTimeLimit() <= 240 &&
            q.getMaxAttempts() >= 0,
          "Cấu hình bài thi không hợp lệ"
        );
        Object raw = d.get("questions");
        check(
          raw instanceof List<?> && !((List<?>) raw).isEmpty(),
          "Cần ít nhất một câu hỏi"
        );
        q.getQuizQuestions().clear();
        int idx = 0;
        for (Object item : (List<?>) raw) {
          Map<String, Object> row = (Map<String, Object>) item;
          QuizQuestion question = new QuizQuestion();
          question.setQuiz(q);
          question.setContent(required(row, "content"));
          question.setAudioUrl(url(row, "audioUrl"));
          question.setOrderIndex(idx++);
          question.setQuestionType("SINGLE_CHOICE");
          Object opts = row.get("options");
          check(
            opts instanceof List<?> && ((List<?>) opts).size() >= 2,
            "Mỗi câu cần ít nhất hai đáp án"
          );
          int count = 0;
          for (Object o : (List<?>) opts) {
            Map<String, Object> od = (Map<String, Object>) o;
            QuizOption option = new QuizOption();
            option.setQuizquestion(question);
            option.setContent(required(od, "content"));
            option.setIsCorrect(bool(od, "correct", false));
            if (option.getIsCorrect()) count++;
            question.getQuizOptions().add(option);
          }
          check(count == 1, "Mỗi câu phải có đúng một đáp án đúng");
          q.getQuizQuestions().add(question);
        }
        if (id == null) data.save(q);
        em.flush();
        int chapter = number(d, "chapterId", 0);
        var links = em
          .createQuery(
            "from CourseContent x where x.quiz=:q",
            CourseContent.class
          )
          .setParameter("q", q)
          .getResultList();
        if (chapter > 0) {
          CourseContent link = links.isEmpty()
            ? new CourseContent()
            : links.get(0);
          link.setQuiz(q);
          link.setChapter(data.find(Chapter.class, chapter));
          link.setContentType("QUIZ");
          link.setOrderIndex(number(d, "orderIndex", 0));
          if (links.isEmpty()) data.save(link);
        } else {
          for (var link : links) em.remove(link);
        }
        return quizView(q, true);
      }
      case "posts": {
        PlatformPost p =
          id == null ? new PlatformPost() : data.find(PlatformPost.class, id);
        p.setTitle(required(d, "title"));
        p.setContent(required(d, "content"));
        String t = required(d, "type");
        check(
          Set.of("BLOG", "BANNER", "NOTICE").contains(t),
          "Loại bài viết không hợp lệ"
        );
        p.setType(t);
        p.setActive(bool(d, "active", true));
        p.setImageUrl(url(d, "imageUrl"));
        if (id == null) data.save(p);
        return postView(p);
      }
      case "vouchers": {
        Voucher v = id == null ? new Voucher() : data.find(Voucher.class, id);
        if (id != null) em.lock(v, LockModeType.PESSIMISTIC_WRITE);
        v.setCode(required(d, "code").toUpperCase(Locale.ROOT));
        String t = required(d, "type");
        check(
          Set.of("PERCENT", "FIXED").contains(t),
          "Loại giảm giá không hợp lệ"
        );
        v.setType(t);
        v.setDiscount(money(d, "discount"));
        check(
          !t.equals("PERCENT") ||
            v.getDiscount().compareTo(BigDecimal.valueOf(100)) <= 0,
          "Giảm phần trăm tối đa 100"
        );
        v.setMinOrder(money(d, "minOrder"));
        v.setMaxDiscount(
          text(d, "maxDiscount").isBlank() ? null : money(d, "maxDiscount")
        );
        v.setExpiredAt(
          text(d, "expiredAt").isBlank()
            ? null
            : LocalDate.parse(text(d, "expiredAt"))
        );
        v.setMaxUsage(number(d, "maxUsage", 100));
        check(
          v.getMaxUsage() >= v.getUsedCount(),
          "Số lượt tối đa nhỏ hơn số đã dùng"
        );
        v.setActive(bool(d, "active", true));
        if (id == null) data.save(v);
        return voucherView(v);
      }
      case "users": {
        User u = id == null ? new User() : data.find(User.class, id);
        check(
          !Objects.equals(u.getUserId(), currentUser.get().getUserId()) ||
            ("ADMIN".equals(text(d, "role")) && bool(d, "active", true)),
          "Không thể khóa hoặc hạ quyền tài khoản đang sử dụng"
        );
        u.setFullName(required(d, "fullName"));
        String email = required(d, "email").toLowerCase(Locale.ROOT);
        check(
          email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"),
          "Email không hợp lệ"
        );
        u.setEmail(email);
        u.setPhone(text(d, "phone"));
        u.setAddress(text(d, "address"));
        String oldRole = u.getRole() == null ? "" : u.getRole().getRoleName();
        String role = required(d, "role");
        check(Set.of("ADMIN", "USER").contains(role), "Vai trò không hợp lệ");
        u.setRole(
          em
            .createQuery("from Role r where r.roleName=:r", Role.class)
            .setParameter("r", role)
            .getSingleResult()
        );
        boolean active = bool(d, "active", true);
        if (
          id != null &&
          (!Objects.equals(u.getStatus(), active) || !oldRole.equals(role))
        ) u.setTokenVersion(u.getTokenVersion() + 1);
        u.setStatus(active);
        if (id == null) {
          u.setUserName(required(d, "userName"));
          u.setCreatedAt(LocalDateTime.now());
          String pass = required(d, "password");
          check(
            pass.length() >= 8 && pass.length() <= 72,
            "Mật khẩu từ 8 đến 72 ký tự"
          );
          u.setPassWord(encoder.encode(pass));
          data.save(u);
        }
        return userView(u);
      }
      default:
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  public void adminDelete(String kind, Integer id) {
    switch (kind) {
      case "books": {
        Book b = data.find(Book.class, id);
        check(
          em
            .createQuery(
              "select count(i) from OrderItem i where i.itemType=:t and i.itemId=:id",
              Long.class
            )
            .setParameter("t", OrderItem.ItemType.Book)
            .setParameter("id", id)
            .getSingleResult() == 0,
          "Sách đã có đơn hàng; đặt tồn kho bằng 0 để ngừng bán"
        );
        check(
          em
            .createQuery(
              "select count(c) from CartItem c where lower(c.itemType)='book' and c.itemId=:id",
              Long.class
            )
            .setParameter("id", id)
            .getSingleResult() == 0,
          "Sách đang có trong giỏ hàng"
        );
        em.createQuery(
          "delete from Favorite f where f.itemType='Book' and f.itemId=:id"
        )
          .setParameter("id", id)
          .executeUpdate();
        em.remove(b);
        break;
      }
      case "courses": {
        Course c = data.find(Course.class, id);
        check(
          c.getUserCourses().isEmpty() &&
            em
              .createQuery(
                "select count(i) from OrderItem i where i.itemType=:t and i.itemId=:id",
                Long.class
              )
              .setParameter("t", OrderItem.ItemType.Course)
              .setParameter("id", id)
              .getSingleResult() == 0 &&
            em
              .createQuery(
                "select count(i) from CartItem i where lower(i.itemType)='course' and i.itemId=:id",
                Long.class
              )
              .setParameter("id", id)
              .getSingleResult() == 0,
          "Khóa học đang được sử dụng; hãy ẩn khóa học"
        );
        em.createQuery(
          "delete from Favorite f where f.itemType='Course' and f.itemId=:id"
        )
          .setParameter("id", id)
          .executeUpdate();
        em.remove(c);
        break;
      }
      case "categories": {
        Category c = data.find(Category.class, id);
        check(c.getBooks().isEmpty(), "Danh mục còn sách");
        em.remove(c);
        break;
      }
      case "posts":
        em.remove(data.find(PlatformPost.class, id));
        break;
      case "vouchers": {
        Voucher v = data.find(Voucher.class, id);
        check(v.getUsedCount() == 0, "Mã đã sử dụng; hãy tắt mã");
        em.remove(v);
        break;
      }
      case "flashcards":
        em.remove(data.find(Flashcard.class, id));
        break;
      case "chapters": {
        Chapter c = data.find(Chapter.class, id);
        check(
          c.getCourseContents().isEmpty(),
          "Chương còn nội dung; hãy ẩn chương"
        );
        em.remove(c);
        break;
      }
      case "lessons": {
        Lesson l = data.find(Lesson.class, id);
        check(
          l.getComments().isEmpty() &&
            em
              .createQuery(
                "select count(p) from UserLessonProgress p where p.lesson=:l",
                Long.class
              )
              .setParameter("l", l)
              .getSingleResult() == 0,
          "Bài học đã có hoạt động; hãy ẩn chương để giữ lịch sử"
        );
        check(
          l
            .getCourseContents()
            .stream()
            .allMatch(x ->
              x.getChapter().getCourse().getUserCourses().isEmpty()
            ),
          "Khóa học đã có người học; không xóa bài học"
        );
        for (var link : new ArrayList<>(l.getCourseContents())) em.remove(link);
        em.flush();
        em.remove(l);
        break;
      }
      case "quizzes": {
        Quiz q = data.find(Quiz.class, id);
        check(
          q.getQuizResults().isEmpty() && q.getCourseContents().isEmpty(),
          "Đề đang được sử dụng"
        );
        check(
          em
            .createQuery(
              "select count(s) from QuizSession s where s.quizId=:q",
              Long.class
            )
            .setParameter("q", id)
            .getSingleResult() == 0,
          "Đề đã có lượt thi"
        );
        em.remove(q);
        break;
      }
      default:
        throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Không hỗ trợ xóa mục này"
        );
    }
    em.flush();
  }

  public Map<String, Object> quizVisibility(Integer id, boolean active) {
    Quiz q = data.find(Quiz.class, id);
    q.setActive(active);
    return quizView(q, true);
  }

  public Map<String, Object> cloneQuiz(Integer id) {
    var copy = new LinkedHashMap<>(quizView(data.find(Quiz.class, id), true));
    String title = Objects.toString(copy.get("title"), "Đề thi");
    copy.put(
      "title",
      title.substring(0, Math.min(title.length(), 220)) + " — Bản sao"
    );
    copy.put("chapterId", null);
    copy.put("active", false);
    return adminSave("quizzes", null, copy);
  }
}
