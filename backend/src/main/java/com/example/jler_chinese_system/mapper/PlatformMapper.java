package com.example.jler_chinese_system.mapper;

import static com.example.jler_chinese_system.common.ResponseMaps.map;
import static com.example.jler_chinese_system.utils.RequestValues.price;

import com.example.jler_chinese_system.entity.*;
import java.util.*;

public final class PlatformMapper {

  private PlatformMapper() {}

  public static Map<String, Object> userView(User u) {
    return map(
      "id",
      u.getUserId(),
      "userName",
      u.getUserName(),
      "email",
      u.getEmail(),
      "fullName",
      u.getFullName(),
      "phone",
      u.getPhone(),
      "address",
      u.getAddress(),
      "avatar",
      u.getAvatarrUrl(),
      "role",
      u.getRole().getRoleName(),
      "active",
      u.getStatus(),
      "createdAt",
      u.getCreatedAt()
    );
  }

  public static Map<String, Object> bookView(Book b) {
    return map(
      "id",
      b.getBookId(),
      "title",
      b.getTitle(),
      "description",
      b.getDescription(),
      "price",
      b.getPrice(),
      "discountPrice",
      b.getDiscountPrice(),
      "stock",
      b.getStock(),
      "categoryIds",
      b.getCategories().stream().map(Category::getCategoryId).sorted().toList(),
      "categories",
      b.getCategories().stream().map(Category::getNameCategory).toList(),
      "imageUrl",
      b
        .getBookImages()
        .stream()
        .sorted(
          Comparator.comparing(
            (BookImage x) -> !Boolean.TRUE.equals(x.getIsThumbNail())
          ).thenComparing(BookImage::getBookImageId)
        )
        .map(BookImage::getImageUrl)
        .findFirst()
        .orElse(""),
      "images",
      b
        .getBookImages()
        .stream()
        .sorted(
          Comparator.comparing(
            (BookImage x) -> !Boolean.TRUE.equals(x.getIsThumbNail())
          ).thenComparing(BookImage::getBookImageId)
        )
        .map(BookImage::getImageUrl)
        .toList()
    );
  }

  public static Map<String, Object> courseView(Course c) {
    return map(
      "id",
      c.getCourseId(),
      "title",
      c.getTitle(),
      "description",
      c.getDescription(),
      "price",
      c.getPrice(),
      "discountPrice",
      c.getDiscountPrice(),
      "imageUrl",
      c.getThumbnailUrl(),
      "courseType",
      c.getCourseType(),
      "active",
      !"HIDDEN".equals(c.getStatus()),
      "chapterCount",
      c.getChapters().size()
    );
  }

  public static Map<String, Object> lessonView(Lesson l, boolean allowed) {
    return map(
      "id",
      l.getLessonId(),
      "title",
      l.getTitle(),
      "duration",
      l.getDuration(),
      "content",
      allowed ? l.getContent() : null,
      "isFree",
      l.getIsFree(),
      "videoUrl",
      allowed ? l.getVideoUrl() : null,
      "attachmentUrl",
      allowed ? l.getAttachmentUrl() : null
    );
  }

  public static Map<String, Object> quizView(Quiz q, boolean answers) {
    return map(
      "id",
      q.getQuizId(),
      "title",
      q.getTitle(),
      "active",
      q.getActive(),
      "quizType",
      q.getQuizType(),
      "passScore",
      q.getPassScore(),
      "timeLimit",
      q.getTimeLimit(),
      "maxAttempts",
      q.getMaxAttempts(),
      "questionCount",
      q.getQuizQuestions().size(),
      "chapterId",
      q
        .getCourseContents()
        .stream()
        .findFirst()
        .map(x -> x.getChapter().getChapterId())
        .orElse(null),
      "orderIndex",
      q
        .getCourseContents()
        .stream()
        .findFirst()
        .map(CourseContent::getOrderIndex)
        .orElse(0),
      "questions",
      answers
        ? q
            .getQuizQuestions()
            .stream()
            .sorted(Comparator.comparing(QuizQuestion::getOrderIndex))
            .map(x -> questionView(x, true))
            .toList()
        : List.of()
    );
  }

  public static Map<String, Object> questionView(
    QuizQuestion q,
    boolean answers
  ) {
    return map(
      "id",
      q.getQuizQuestionId(),
      "content",
      q.getContent(),
      "audioUrl",
      q.getAudioUrl(),
      "orderIndex",
      q.getOrderIndex(),
      "options",
      q
        .getQuizOptions()
        .stream()
        .map(o ->
          answers
            ? map(
                "id",
                o.getQuizOptionId(),
                "content",
                o.getContent(),
                "correct",
                o.getIsCorrect()
              )
            : map("id", o.getQuizOptionId(), "content", o.getContent())
        )
        .toList()
    );
  }

  public static Map<String, Object> postView(PlatformPost p) {
    return map(
      "id",
      p.getId(),
      "title",
      p.getTitle(),
      "content",
      p.getContent(),
      "imageUrl",
      p.getImageUrl(),
      "type",
      p.getType(),
      "active",
      p.getActive(),
      "createdAt",
      p.getCreatedAt()
    );
  }

  public static Map<String, Object> voucherView(Voucher v) {
    return map(
      "id",
      v.getId(),
      "code",
      v.getCode(),
      "type",
      v.getType(),
      "discount",
      v.getDiscount(),
      "minOrder",
      v.getMinOrder(),
      "maxDiscount",
      v.getMaxDiscount(),
      "expiredAt",
      v.getExpiredAt(),
      "maxUsage",
      v.getMaxUsage(),
      "usedCount",
      v.getUsedCount(),
      "active",
      v.getActive()
    );
  }

  public static Map<String, Object> flashView(Flashcard f) {
    return map(
      "id",
      f.getFlashcardId(),
      "lessonId",
      f.getLesson() == null ? null : f.getLesson().getLessonId(),
      "front",
      f.getFronttext(),
      "back",
      f.getBacktext(),
      "imageUrl",
      f.getImageUrl()
    );
  }

  public static List<?> chapters(Course c, boolean allowed) {
    return c
      .getChapters()
      .stream()
      .filter(ch -> Boolean.TRUE.equals(ch.getIsActive()))
      .sorted(Comparator.comparing(Chapter::getOrderIndex))
      .map(ch ->
        map(
          "id",
          ch.getChapterId(),
          "title",
          ch.getTitle(),
          "orderIndex",
          ch.getOrderIndex(),
          "contents",
          ch
            .getCourseContents()
            .stream()
            .filter(
              x ->
                x.getQuiz() == null ||
                Boolean.TRUE.equals(x.getQuiz().getActive())
            )
            .sorted(Comparator.comparing(CourseContent::getOrderIndex))
            .map(x ->
              map(
                "id",
                x.getCourseContentId(),
                "type",
                x.getContentType(),
                "lesson",
                x.getLesson() == null
                  ? null
                  : lessonView(
                      x.getLesson(),
                      allowed ||
                        Boolean.TRUE.equals(x.getLesson().getIsFree()) ||
                        Boolean.TRUE.equals(x.getIsPreview())
                    ),
                "quiz",
                x.getQuiz() == null ? null : quizView(x.getQuiz(), false)
              )
            )
            .toList()
        )
      )
      .toList();
  }

  public static Map<String, Object> orderView(Order o) {
    return map(
      "id",
      o.getOrderId(),
      "code",
      o.getOrderCode(),
      "userId",
      o.getUser().getUserId(),
      "customer",
      o.getUser().getFullName(),
      "createdAt",
      o.getCreatedAt(),
      "status",
      o.getStatus(),
      "paymentMethod",
      o.getPaymentMethod(),
      "paymentStatus",
      o.getPaymentStatus(),
      "subtotal",
      o.getSubtotal(),
      "discount",
      o.getDiscount(),
      "shippingFee",
      o.getShippingFee(),
      "total",
      o.getTotalAmount(),
      "note",
      o.getNote(),
      "recipient",
      o.getShippingOrder() == null
        ? null
        : map(
            "fullName",
            o.getShippingOrder().getFullName(),
            "phone",
            o.getShippingOrder().getPhone(),
            "address",
            o.getShippingOrder().getAddress()
          ),
      "items",
      o
        .getOderItems()
        .stream()
        .map(i ->
          map(
            "id",
            i.getOrderItemId(),
            "itemId",
            i.getItemId(),
            "itemType",
            i.getItemType(),
            "title",
            i.getItemTitle(),
            "price",
            i.getPriceAtPurchase(),
            "quantity",
            i.getQuantity()
          )
        )
        .toList()
    );
  }

  public static Map<String, Object> resultView(QuizResult r) {
    return map(
      "id",
      r.getQuizResultsId(),
      "quizId",
      r.getQuiz().getQuizId(),
      "title",
      r.getQuiz().getTitle(),
      "score",
      r.getScore(),
      "correct",
      r.getCorrectAnswers(),
      "total",
      r.getTotalQuestions(),
      "status",
      r.getStatus(),
      "timeSpent",
      r.getTimeSpent(),
      "submittedAt",
      r.getSubmittedAt(),
      "details",
      r
        .getAttemptDetails()
        .stream()
        .map(a ->
          map(
            "question",
            questionView(a.getQuizQuestion(), true),
            "selected",
            a.getSelectedOption() == null
              ? null
              : a.getSelectedOption().getQuizOptionId(),
            "correct",
            a.getIsCorrect()
          )
        )
        .toList()
    );
  }
}
